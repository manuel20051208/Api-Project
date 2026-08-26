package com.apiproject.services.general;

import com.apiproject.DTOs.Admin.NotificationEventDTO;
import com.apiproject.DTOs.General.*;
import com.apiproject.entities.admin.SecondHandCupon;
import com.apiproject.entities.admin.ShCuponUsedByClients;
import com.apiproject.entities.client.UserClient;
import com.apiproject.entities.general.SecondHandProduct;
import com.apiproject.entities.general.ShSale;
import com.apiproject.entities.general.ShSalesItem;
import com.apiproject.enums.Status;
import com.apiproject.exceptions.ResourceNotFoundException;
import com.apiproject.repositories.admin.SecondHandCuponRepository;
import com.apiproject.repositories.client.ClientRepository;
import com.apiproject.repositories.client.PaymentCardRepository;
import com.apiproject.repositories.general.ShSaleItemRepository;
import com.apiproject.repositories.general.ShSaleRepository;
import com.apiproject.repositories.general.SecondHandProductRepository;
import com.apiproject.services.admin.NotificationService;
import com.apiproject.services.admin.SecondHandCuponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
public class ShSaleService {
    private final NotificationService notificationService;
    private final SecondHandProductRepository secondHandProductRepository;
    private final ShSaleRepository shSaleRepository;
    private final ShSaleItemRepository shSaleItemRepository;
    private final ClientRepository clientRepository;
    private final PaymentCardRepository paymentCardRepository;
    private final SecondHandCuponService secondHandCuponService;
    private final SecondHandCuponRepository secondHandCuponRepository;

    @Transactional
    public ShPurchaseResponseDTO purchase(ShPurchaseRequestDTO requestDTO, Long authenticatedClientId) {
        validatePurchaseRequest(requestDTO, authenticatedClientId);

        UserClient client = clientRepository.findById(authenticatedClientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found: " + authenticatedClientId));

        // Regla igual a la compra normal: se requiere tarjeta simulada activa.
        if (!paymentCardRepository.existsByUserClientIdAndActiveTrue(requestDTO.clientId())) {
            throw new ResponseStatusException(PAYMENT_REQUIRED, "El cliente no tiene una tarjeta activa");
        }

        Map<Long, Integer> requestedQuantities = groupRequestedQuantities(requestDTO.items());
        List<Long> productIds = requestedQuantities.keySet().stream().sorted().toList();

        // Bloquea los productos pedidos para evitar ventas concurrentes sobre el mismo stock.
        Map<Long, SecondHandProduct> productsById = secondHandProductRepository.findAllByIdInForUpdate(productIds)
                .stream()
                .collect(Collectors.toMap(SecondHandProduct::getId, Function.identity()));

        if (productsById.size() != requestedQuantities.size()) {
            throw new ResourceNotFoundException("One or more second hand products were not found");
        }

        LocalDateTime now = LocalDateTime.now();
        BigDecimal originalTotal = BigDecimal.ZERO;
        List<ShSaleDraft> saleDrafts = new ArrayList<>(requestedQuantities.size());
        Set<Long> affectedAdminIds = new LinkedHashSet<>();

        // Valida y descuenta stock; arma un borrador de venta por producto.
        for (Map.Entry<Long, Integer> entry : requestedQuantities.entrySet()) {
            SecondHandProduct product = productsById.get(entry.getKey());
            int quantity = entry.getValue();

            validateProductForPurchase(product, quantity);

            product.setStock(product.getStock() - quantity);
            BigDecimal subtotal = unitPrice(product).multiply(BigDecimal.valueOf(quantity));

            originalTotal = originalTotal.add(subtotal);
            affectedAdminIds.add(product.getUserAdmin().getId());
            saleDrafts.add(new ShSaleDraft(product, quantity, subtotal, product.getUserAdmin()));
        }

        // El cupon (opcional) debe cubrir TODOS los productos del carrito y ser del mismo dueño.
        SecondHandCuponService.CouponResolution coupon = null;
        if (requestDTO.cuponCode() != null && !requestDTO.cuponCode().isBlank()) {
            Set<Long> ownerIds = saleDrafts.stream()
                    .map(draft -> draft.owner().getId())
                    .collect(Collectors.toSet());
            if (ownerIds.size() > 1) {
                throw new ResponseStatusException(CONFLICT,
                        "El cupon no puede aplicarse a un carrito de varios vendedores");
            }
            coupon = secondHandCuponService.resolveForCart(
                    requestDTO.cuponCode(), productIds, ownerIds.iterator().next());
        }

        // Crea una venta individual por producto con el descuento proporcional si hay cupon.
        List<ShSale> sales = new ArrayList<>(saleDrafts.size());
        for (ShSaleDraft draft : saleDrafts) {
            ShSale sale = new ShSale();
            sale.setUserClient(client);
            sale.setHora(now);
            sale.setUserAdmin(draft.owner());
            sale.setTotalAmount(applyDiscount(draft.subtotal(), coupon));
            sales.add(sale);
        }

        List<ShSale> savedSales = shSaleRepository.saveAll(sales);

        List<ShSalesItem> saleItems = new ArrayList<>(saleDrafts.size());
        for (int i = 0; i < saleDrafts.size(); i++) {
            ShSaleDraft draft = saleDrafts.get(i);
            saleItems.add(buildSaleItem(savedSales.get(i), client, draft.product(), draft.quantity(), now));
        }
        shSaleItemRepository.saveAll(saleItems);

        // Canjea el cupon una sola vez por compra y registra el uso del cliente.
        if (coupon != null) {
            secondHandCuponService.redeem(coupon);

            SecondHandCupon cuponRef = secondHandCuponRepository.getReferenceById(coupon.cuponId());
            ShCuponUsedByClients usage = new ShCuponUsedByClients();
            usage.setClientUser(client);
            usage.setSale(savedSales.getFirst());
            usage.setCupon(cuponRef);
            usage.setCreatedAt(now);
            secondHandCuponService.registerUsage(usage);
        }

        List<ShPurchaseItemResponseDTO> responseItems = saleDrafts.stream()
                .map(draft -> new ShPurchaseItemResponseDTO(
                        draft.product().getId(),
                        draft.product().getName(),
                        draft.quantity(),
                        unitPrice(draft.product()),
                        draft.subtotal()))
                .toList();

        // Notifica a cada admin afectado y avisa stock bajo.
        for (ShSaleDraft draft : saleDrafts) {
            Long ownerId = draft.owner().getId();
            notificationService.push(ownerId, new NotificationEventDTO(
                    "VENTA_NUEVA_SH",
                    "Nueva venta segunda mano por $" + draft.subtotal(),
                    ownerId
            ));
            if (draft.product().getStock() <= 5) {
                notificationService.push(ownerId, new NotificationEventDTO(
                        "STOCK_BAJO",
                        "Stock bajo: " + draft.product().getName() + " (" + draft.product().getStock() + " restantes)",
                        ownerId
                ));
            }
        }

        List<Long> saleIds = savedSales.stream().map(ShSale::getId).toList();
        BigDecimal totalAmount = sales.stream()
                .map(ShSale::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal discountApplied = originalTotal.subtract(totalAmount).setScale(2, RoundingMode.HALF_UP);

        return new ShPurchaseResponseDTO(
                saleIds,
                requestDTO.clientId(),
                coupon == null ? null : requestDTO.cuponCode(),
                originalTotal,
                discountApplied,
                totalAmount,
                now,
                responseItems
        );
    }

    /** Historial de compras segunda mano del cliente autenticado. */
    @Transactional(readOnly = true)
    public List<com.apiproject.repositories.projection.ShSaleHistoryProjection> clientHistory(Long authenticatedClientId) {
        return shSaleItemRepository.findClientHistory(authenticatedClientId);
    }

    /** Historial de ventas segunda mano para un admin (opcionalmente filtrado por cliente). */
    @Transactional(readOnly = true)
    public List<com.apiproject.repositories.projection.ShSaleHistoryProjection> adminHistory(Long adminId, Long clientId) {
        return shSaleItemRepository.findAdminHistory(adminId, clientId);
    }

    // ================= Helpers =================

    private void validatePurchaseRequest(ShPurchaseRequestDTO requestDTO, Long authenticatedClientId) {
        if (requestDTO == null) {
            throw new ResponseStatusException(BAD_REQUEST, "El cuerpo de la solicitud es obligatorio");
        }
        if (!requestDTO.clientId().equals(authenticatedClientId)) {
            throw new ResponseStatusException(FORBIDDEN, "No puedes comprar usando otro cliente");
        }
        if (requestDTO.clientId() == null) {
            throw new ResponseStatusException(BAD_REQUEST, "clientId es obligatorio");
        }
        if (requestDTO.items() == null || requestDTO.items().isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "La compra debe tener al menos un producto");
        }
    }

    private Map<Long, Integer> groupRequestedQuantities(List<ShPurchaseItemRequestDTO> items) {
        Map<Long, Integer> quantities = new LinkedHashMap<>();
        for (ShPurchaseItemRequestDTO item : items) {
            if (item == null || item.productId() == null) {
                throw new ResponseStatusException(BAD_REQUEST, "Cada item debe tener productId");
            }
            if (item.quantity() == null || item.quantity() <= 0) {
                throw new ResponseStatusException(BAD_REQUEST, "La cantidad debe ser mayor a cero");
            }
            quantities.merge(item.productId(), item.quantity(), Integer::sum);
        }
        return quantities;
    }

    private void validateProductForPurchase(SecondHandProduct product, Integer quantity) {
        if (!product.isActive()) {
            throw new ResponseStatusException(CONFLICT, "Product is not active: " + product.getId());
        }
        if (product.getUserAdmin() == null) {
            throw new ResponseStatusException(CONFLICT, "Product has no owner: " + product.getId());
        }
        if (product.getStock() == null || product.getStock() < quantity) {
            throw new ResponseStatusException(CONFLICT, "Insufficient stock for product: " + product.getName());
        }
    }

    private ShSalesItem buildSaleItem(ShSale sale, UserClient client, SecondHandProduct product,
                                      Integer quantity, LocalDateTime date) {
        ShSalesItem item = new ShSalesItem();
        item.setShSale(sale);
        item.setUserClient(client);
        item.setShProduct(product);
        item.setQuantity(quantity);
        item.setState(Status.COMPLETED);
        item.setDate(date);
        return item;
    }

    private BigDecimal applyDiscount(BigDecimal subtotal, SecondHandCuponService.CouponResolution coupon) {
        if (coupon == null) {
            return subtotal.setScale(2, RoundingMode.HALF_UP);
        }
        BigDecimal discountFactor = BigDecimal.valueOf(coupon.discountPercent())
                .divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);
        return subtotal.subtract(subtotal.multiply(discountFactor))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal unitPrice(SecondHandProduct product) {
        return BigDecimal.valueOf(product.getPrice());
    }

    private record ShSaleDraft(
            SecondHandProduct product,
            Integer quantity,
            BigDecimal subtotal,
            com.apiproject.entities.admin.UserAdmin owner
    ) {
    }
}
