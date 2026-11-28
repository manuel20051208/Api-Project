package com.apiproject.services.general;

import com.apiproject.DTOs.General.CancelSaleRequestDTO;
import com.apiproject.DTOs.General.CancelSaleResponseDTO;
import com.apiproject.config.CacheConstants;
import com.apiproject.entities.general.Product;
import com.apiproject.entities.general.Sale;
import com.apiproject.entities.general.SalesItem;
import com.apiproject.entities.general.SecondHandProduct;
import com.apiproject.entities.general.ShSale;
import com.apiproject.entities.general.ShSalesItem;
import com.apiproject.enums.SaleStatus;
import com.apiproject.enums.Status;
import com.apiproject.exceptions.ResourceNotFoundException;
import com.apiproject.repositories.general.ProductRepository;
import com.apiproject.repositories.general.SaleItemRepository;
import com.apiproject.repositories.general.SaleRepository;
import com.apiproject.repositories.general.SecondHandProductRepository;
import com.apiproject.repositories.general.ShSaleItemRepository;
import com.apiproject.repositories.general.ShSaleRepository;
import com.apiproject.security.AuthenticatedUser;
import com.apiproject.services.admin.CuponService;
import com.apiproject.services.admin.SecondHandCuponService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;

/**
 * Cancelacion total o parcial (por item) de ventas normales y de segunda mano.
 * Reglas: el ADMIN dueno de la venta puede cancelar siempre; el CLIENT dueno solo dentro de las
 * CLIENT_CANCEL_WINDOW_HOURS horas siguientes a la compra. Cada item cancelado devuelve su stock y
 * reembolsa unitPrice*quantity - descuento del item. El cupon solo se restituye si la venta queda CANCELADA completa.
 */
@Service
@RequiredArgsConstructor
public class SaleCancellationService {
    public static final long CLIENT_CANCEL_WINDOW_HOURS = 24;

    private final SaleRepository saleRepository;
    private final SaleItemRepository saleItemRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;
    private final CuponService cuponService;
    private final ShSaleRepository shSaleRepository;
    private final ShSaleItemRepository shSaleItemRepository;
    private final SecondHandProductRepository secondHandProductRepository;
    private final SecondHandCuponService secondHandCuponService;

    // ================= Ventas normales =================

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheConstants.CLIENT_HISTORY, allEntries = true),
            @CacheEvict(value = CacheConstants.DASHBOARD, allEntries = true),
            @CacheEvict(value = CacheConstants.DASHBOARD_CLIENTS, allEntries = true),
            @CacheEvict(value = CacheConstants.CLIENTS, allEntries = true),
            @CacheEvict(value = CacheConstants.SALES, allEntries = true),
            @CacheEvict(value = CacheConstants.SALE_SUMMARY, allEntries = true),
            @CacheEvict(value = CacheConstants.BEST_CLIENTS, allEntries = true),
            @CacheEvict(value = CacheConstants.BEST_PRODUCTS, allEntries = true)
    })
    public CancelSaleResponseDTO cancelSale(Long saleId, CancelSaleRequestDTO request, AuthenticatedUser user) {
        // FOR UPDATE: dos cancelaciones simultaneas de la misma venta se serializan.
        Sale sale = saleRepository.findByIdForUpdate(saleId)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found: " + saleId));

        requireCancellationPermission(user,
                sale.getUserAdmin() == null ? null : sale.getUserAdmin().getId(),
                sale.getUserClient() == null ? null : sale.getUserClient().getId(),
                sale.getHora());

        List<SalesItem> allItems = saleItemRepository.findBySalesId(saleId);
        List<SalesItem> toCancel = selectItemsToCancel(sale.getStatus(), allItems, request,
                SalesItem::getId, SalesItem::getState, SalesItem::getCancelledAt);

        // Bloquea los productos (orden estable por id) antes de devolver stock.
        List<Long> productIds = toCancel.stream().map(i -> i.getProduct().getId()).distinct().sorted().toList();
        Map<Long, Product> products = productRepository.findAllByIdInForUpdate(productIds).stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        LocalDateTime now = LocalDateTime.now();
        BigDecimal refundedNow = BigDecimal.ZERO;
        for (SalesItem item : toCancel) {
            Product product = products.get(item.getProduct().getId());
            if (product != null) {
                product.setStock(product.getStock() + item.getQuantity());
            }
            item.setState(Status.CANCELLED);
            item.setCancelledAt(now);
            refundedNow = refundedNow.add(itemRefund(item.getUnitPrice(), item.getQuantity(), item.getDiscount()));
        }
        saleItemRepository.saveAll(toCancel);

        sale.setRefundedAmount(sale.getRefundedAmount().add(refundedNow).setScale(2, RoundingMode.HALF_UP));
        boolean allCancelled = allItems.stream()
                .allMatch(i -> i.getState() == Status.CANCELLED || i.getCancelledAt() != null);
        sale.setStatus(allCancelled ? SaleStatus.CANCELLED : SaleStatus.PARTIALLY_CANCELLED);
        saleRepository.save(sale);

        // El cupon solo vuelve si la venta quedo cancelada por completo.
        if (allCancelled && sale.getCupon() != null) {
            cuponService.restoreUsage(sale.getCupon().getId(), sale.getId());
        }

        // El stock cambio: refresca las caches de producto igual que la compra.
        productService.refreshCachesAfterStockChange(products.values());

        return toResponse(sale.getId(), sale.getStatus(), refundedNow, sale.getRefundedAmount(),
                sale.getTotalAmount(), toCancel.stream().map(SalesItem::getId).toList());
    }

    // ================= Ventas segunda mano =================

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheConstants.SALE_SUMMARY, allEntries = true),
            @CacheEvict(value = CacheConstants.BEST_CLIENTS, allEntries = true),
            @CacheEvict(value = CacheConstants.BEST_PRODUCTS, allEntries = true)
    })
    public CancelSaleResponseDTO cancelShSale(Long saleId, CancelSaleRequestDTO request, AuthenticatedUser user) {
        ShSale sale = shSaleRepository.findByIdForUpdate(saleId)
                .orElseThrow(() -> new ResourceNotFoundException("Second hand sale not found: " + saleId));

        requireCancellationPermission(user,
                sale.getUserAdmin() == null ? null : sale.getUserAdmin().getId(),
                sale.getUserClient() == null ? null : sale.getUserClient().getId(),
                sale.getHora());

        List<ShSalesItem> allItems = shSaleItemRepository.findByShSaleId(saleId);
        List<ShSalesItem> toCancel = selectItemsToCancel(sale.getStatus(), allItems, request,
                ShSalesItem::getId, ShSalesItem::getState, ShSalesItem::getCancelledAt);

        List<Long> productIds = toCancel.stream().map(i -> i.getShProduct().getId()).distinct().sorted().toList();
        Map<Long, SecondHandProduct> products = secondHandProductRepository.findAllByIdInForUpdate(productIds).stream()
                .collect(Collectors.toMap(SecondHandProduct::getId, Function.identity()));

        LocalDateTime now = LocalDateTime.now();
        BigDecimal refundedNow = BigDecimal.ZERO;
        for (ShSalesItem item : toCancel) {
            SecondHandProduct product = products.get(item.getShProduct().getId());
            if (product != null) {
                product.setStock(product.getStock() + item.getQuantity());
            }
            item.setState(Status.CANCELLED);
            item.setCancelledAt(now);
            refundedNow = refundedNow.add(itemRefund(item.getUnitPrice(), item.getQuantity(), item.getDiscount()));
        }
        shSaleItemRepository.saveAll(toCancel);

        sale.setRefundedAmount(sale.getRefundedAmount().add(refundedNow).setScale(2, RoundingMode.HALF_UP));
        boolean allCancelled = allItems.stream()
                .allMatch(i -> i.getState() == Status.CANCELLED || i.getCancelledAt() != null);
        sale.setStatus(allCancelled ? SaleStatus.CANCELLED : SaleStatus.PARTIALLY_CANCELLED);
        shSaleRepository.save(sale);

        if (allCancelled && sale.getCupon() != null) {
            secondHandCuponService.restoreUsage(sale.getCupon().getId(), sale.getId());
        }

        return toResponse(sale.getId(), sale.getStatus(), refundedNow, sale.getRefundedAmount(),
                sale.getTotalAmount(), toCancel.stream().map(ShSalesItem::getId).toList());
    }

    // ================= Helpers =================

    /** ADMIN dueno: siempre. CLIENT dueno: solo dentro de las 24 h de la compra. Cualquier otro: 403. */
    private void requireCancellationPermission(AuthenticatedUser user, Long saleAdminId, Long saleClientId,
                                               LocalDateTime createdAt) {
        if (user == null) {
            throw new ResponseStatusException(FORBIDDEN, "No tienes permiso para cancelar esta venta");
        }
        if (user.isAdmin()) {
            if (saleAdminId == null || !saleAdminId.equals(user.id())) {
                throw new ResponseStatusException(FORBIDDEN, "No puedes cancelar ventas de otro admin");
            }
            return;
        }
        if (user.isClient()) {
            if (saleClientId == null || !saleClientId.equals(user.id())) {
                throw new ResponseStatusException(FORBIDDEN, "No puedes cancelar compras de otro cliente");
            }
            if (createdAt == null
                    || Duration.between(createdAt, LocalDateTime.now()).toHours() >= CLIENT_CANCEL_WINDOW_HOURS) {
                throw new ResponseStatusException(FORBIDDEN,
                        "Solo puedes cancelar tu compra dentro de las " + CLIENT_CANCEL_WINDOW_HOURS
                                + " horas siguientes a realizarla");
            }
            return;
        }
        throw new ResponseStatusException(FORBIDDEN, "No tienes permiso para cancelar esta venta");
    }

    /**
     * Resuelve que items se cancelan: sin ids = todos los pendientes; con ids = esos (400 si alguno no es de la
     * venta). 409 si la venta ya esta CANCELLED, si un item pedido ya estaba cancelado o si no queda nada por cancelar.
     */
    private <T> List<T> selectItemsToCancel(SaleStatus saleStatus, List<T> allItems, CancelSaleRequestDTO request,
                                            Function<T, Long> idOf, Function<T, Status> stateOf,
                                            Function<T, LocalDateTime> cancelledAtOf) {
        List<Long> requestedIds = request == null || request.itemIds() == null
                ? List.of()
                : request.itemIds().stream().filter(Objects::nonNull).distinct().toList();

        Map<Long, T> byId = allItems.stream().collect(Collectors.toMap(idOf, Function.identity()));
        for (Long id : requestedIds) {
            if (!byId.containsKey(id)) {
                throw new ResponseStatusException(BAD_REQUEST, "El item " + id + " no pertenece a esta venta");
            }
        }
        if (saleStatus == SaleStatus.CANCELLED) {
            throw new ResponseStatusException(CONFLICT, "La venta ya esta cancelada");
        }

        List<T> selected;
        if (requestedIds.isEmpty()) {
            selected = allItems.stream()
                    .filter(i -> stateOf.apply(i) != Status.CANCELLED && cancelledAtOf.apply(i) == null)
                    .toList();
        } else {
            selected = requestedIds.stream().map(byId::get).toList();
            for (T item : selected) {
                if (stateOf.apply(item) == Status.CANCELLED || cancelledAtOf.apply(item) != null) {
                    throw new ResponseStatusException(CONFLICT, "El item " + idOf.apply(item) + " ya esta cancelado");
                }
            }
        }
        if (selected.isEmpty()) {
            throw new ResponseStatusException(CONFLICT, "No hay items por cancelar en la venta");
        }
        return selected;
    }

    /** Reembolso del item: precio historico x cantidad menos su parte del descuento del cupon. */
    private static BigDecimal itemRefund(BigDecimal unitPrice, Integer quantity, BigDecimal discount) {
        BigDecimal gross = unitPrice.multiply(BigDecimal.valueOf(quantity));
        BigDecimal disc = discount == null ? BigDecimal.ZERO : discount;
        return gross.subtract(disc).setScale(2, RoundingMode.HALF_UP);
    }

    private static CancelSaleResponseDTO toResponse(Long saleId, SaleStatus status, BigDecimal refundedNow,
                                                    BigDecimal refundedTotal, BigDecimal totalAmount,
                                                    List<Long> cancelledItemIds) {
        return new CancelSaleResponseDTO(
                saleId,
                status,
                refundedNow.setScale(2, RoundingMode.HALF_UP),
                refundedTotal,
                totalAmount,
                totalAmount.subtract(refundedTotal).setScale(2, RoundingMode.HALF_UP),
                cancelledItemIds);
    }
}
