package com.apiproject.services.general;

import com.apiproject.DTOs.Admin.NotificationEventDTO;
import com.apiproject.DTOs.General.*;
import com.apiproject.config.CacheConstants;
import com.apiproject.entities.admin.Cupon;
import com.apiproject.entities.admin.CuponUsedByClients;
import com.apiproject.entities.client.UserClient;
import com.apiproject.entities.general.Product;
import com.apiproject.entities.general.Sale;
import com.apiproject.entities.general.SalesItem;
import com.apiproject.enums.Status;
import com.apiproject.exceptions.ResourceNotFoundException;
import com.apiproject.repositories.admin.CuponRepository;
import com.apiproject.repositories.client.ClientRepository;
import com.apiproject.repositories.client.PaymentCardRepository;
import com.apiproject.repositories.general.ProductRepository;
import com.apiproject.repositories.general.SaleItemRepository;
import com.apiproject.repositories.general.SaleRepository;
import com.apiproject.services.admin.CuponService;
import com.apiproject.services.admin.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Caching;
import org.springframework.scheduling.annotation.Scheduled;
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
public class SaleService {
    private final NotificationService notificationService;
    private final SaleItemRepository saleItemRepository;
    private final ProductRepository productRepository;
    private final ClientRepository clientRepository;
    private final PaymentCardRepository paymentCardRepository;
    private final CacheManager cacheManager;
    private final SaleRepository saleRepository;
    private final CuponService cuponService;
    private final CuponRepository cuponRepository;

    @Scheduled(
            initialDelay = 0,
            fixedDelayString = "${app.cache.refresh-ms:20000}"
    )
    @CachePut(
            value = CacheConstants.PRODUCTS_ACTIVE_WITH_IMAGES,
            key = "'all'"
    )
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> refreshClientProductsCache() {
        return productRepository.findAllActiveWithImages()
                .stream()
                .map(ProductResponseDTO::fromEntity)
                .toList();
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheConstants.CLIENT_HISTORY, key = "#authenticatedClientId"),
            @CacheEvict(value = CacheConstants.DASHBOARD, allEntries = true)
    })
    public PurchaseResponseDTO purchase(PurchaseRequestDTO requestDTO, Long authenticatedClientId) {
        // Validamos el cuerpo del request de la venta
        validatePurchaseRequest(requestDTO);

        // Validamos que el cliente que hará la compra es igual al ID del cliente actual autenticado
        if (!requestDTO.clientId().equals(authenticatedClientId)) {
            throw new ResponseStatusException(FORBIDDEN, "No puedes comprar usando otro cliente");
        }

        // Luego de las validaciones anteriores buscamos el cliente
        UserClient client = clientRepository.findById(authenticatedClientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found: " + authenticatedClientId));

        // Validamos que al menos haya una tarjeta activa para realizar compra (tarjeta simulada)
        if (!paymentCardRepository.existsByUserClientIdAndActiveTrue(requestDTO.clientId())) {
            throw new ResponseStatusException(PAYMENT_REQUIRED, "El cliente no tiene una tarjeta activa");
        }

        Map<Long, Integer> requestedQuantities = groupRequestedQuantities(requestDTO.items());
        List<Long> productIds = requestedQuantities.keySet().stream()
                .sorted()
                .toList();

        // Bloquea todos los productos pedidos para evitar ventas concurrentes sobre el mismo stock.
        Map<Long, Product> productsById = productRepository.findAllByIdInForUpdate(
                        productIds)
                .stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        if (productsById.size() != requestedQuantities.size()) {
            throw new ResourceNotFoundException("One or more products were not found");
        }

        LocalDateTime now = LocalDateTime.now();
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<SaleDraft> saleDrafts = new ArrayList<>(requestedQuantities.size());
        Set<Long> affectedAdminIds = new LinkedHashSet<>();

        // Valida y descuenta el stock; arma un borrador de venta por producto/admin.
        for (Map.Entry<Long, Integer> entry : requestedQuantities.entrySet()) {
            Product product = productsById.get(entry.getKey());
            int quantity = entry.getValue();

            validateProductForPurchase(product, quantity);

            product.setStock(product.getStock() - quantity);
            BigDecimal subtotal = unitPrice(product).multiply(BigDecimal.valueOf(quantity));

            totalAmount = totalAmount.add(subtotal);
            affectedAdminIds.add(product.getUserAdmin().getId());
            saleDrafts.add(new SaleDraft(product, quantity, subtotal, product.getUserAdmin().getId()));
        }

        // Cupon opcional: debe cubrir TODOS los productos del carrito y ser del mismo dueño.
        CuponService.CouponResolution coupon = null;
        if (requestDTO.cuponCode() != null && !requestDTO.cuponCode().isBlank()) {
            Set<Long> ownerIds = saleDrafts.stream()
                    .map(SaleDraft::adminId)
                    .collect(Collectors.toSet());
            if (ownerIds.size() > 1) {
                throw new ResponseStatusException(CONFLICT,
                        "El cupon no puede aplicarse a un carrito de varios vendedores");
            }
            coupon = cuponService.resolveForCart(requestDTO.cuponCode(), productIds, ownerIds.iterator().next());
        }

        // Crea una venta individual por producto/admin con el descuento proporcional si hay cupon.
        List<Sale> sales = new ArrayList<>(saleDrafts.size());
        for (SaleDraft draft : saleDrafts) {
            Sale sale = new Sale();
            sale.setUserClient(client);
            sale.setHora(now);
            sale.setUserAdmin(productOwner(draft));
            sale.setTotalAmount(applyDiscount(draft.subtotal(), coupon));
            sales.add(sale);
        }

        // Inserta todas las ventas en un solo batch y asigna los ids generados a cada venta.
        List<Sale> savedSales = saleRepository.saveAll(sales);

        // Crea los items de venta enlazados a las ventas insertadas.
        List<SalesItem> saleItems = new ArrayList<>(saleDrafts.size());
        for (int i = 0; i < saleDrafts.size(); i++) {
            SaleDraft draft = saleDrafts.get(i);
            saleItems.add(buildSaleItem(sales.get(i), client, draft.product(), draft.quantity(), now));
        }
        saleItemRepository.saveAll(saleItems);

        // Canjea el cupon una sola vez por compra y registra el uso del cliente.
        if (coupon != null) {
            cuponService.redeem(coupon);

            Cupon cuponRef = cuponRepository.getReferenceById(coupon.cuponId());
            CuponUsedByClients usage = new CuponUsedByClients();
            usage.setClientUser(client);
            usage.setSale(savedSales.getFirst());
            usage.setCupon(cuponRef);
            usage.setCreatedAt(now);
            cuponService.registerUsage(usage);
        }

        List<PurchaseItemResponseDTO> responseItems = saleItems.stream()
                .map(this::toPurchaseItemResponse)
                .toList();

        for (SaleDraft draft : saleDrafts) {
            notificationService.push(draft.adminId(), new NotificationEventDTO(
                    "VENTA_NUEVA",
                    "Nueva venta por $" + draft.subtotal(),
                    draft.adminId()
            ));
        }

        // Notifica stock bajo al admin dueño del producto despues de descontar la compra.
        for (SaleDraft draft : saleDrafts) {
            Product product = draft.product();
            if (product.getStock() <= 5) {
                notificationService.push(draft.adminId(), new NotificationEventDTO(
                        "STOCK_BAJO",
                        "Stock bajo: " + product.getName() + " (" + product.getStock() + " restantes)",
                        draft.adminId()
                ));
            }
        }

        // Limpia las caches de cada admin afectado al final de la compra.
        affectedAdminIds.forEach(this::evictAdminCaches);

        // Devuelve todos los ids de ventas creadas para que el front conozca las ventas internas.
        List<Long> saleIds = savedSales.stream()
                .map(Sale::getId)
                .toList();

        BigDecimal finalTotal = sales.stream()
                .map(Sale::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Retornamos la venta
        return new PurchaseResponseDTO(
                saleIds.getFirst(),
                saleIds,
                requestDTO.clientId(),
                coupon == null ? null : requestDTO.cuponCode(),
                totalAmount,
                totalAmount.subtract(finalTotal),
                finalTotal,
                now,
                responseItems);
    }

    private com.apiproject.entities.admin.UserAdmin productOwner(SaleDraft draft) {
        return draft.product().getUserAdmin();
    }

    private BigDecimal applyDiscount(BigDecimal subtotal, CuponService.CouponResolution coupon) {
        if (coupon == null) {
            return subtotal.setScale(2, RoundingMode.HALF_UP);
        }
        BigDecimal discountFactor = BigDecimal.valueOf(coupon.discountPercent())
                .divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);
        return subtotal.subtract(subtotal.multiply(discountFactor))
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Método de refrescar cache si hay datos ya guardados en esa
     * cache para traer datos nuevos a la memoria
     */
    private void evictAdminCaches(Long adminId) {
        if (adminId == null) return;
        evictIfPresent(CacheConstants.DASHBOARD, adminId);
        evictIfPresent(CacheConstants.DASHBOARD_CLIENTS, adminId);
        evictIfPresent(CacheConstants.SALES, adminId);
        evictIfPresent(CacheConstants.CLIENTS, adminId);
    }

    // Evict caché si ya hay datos almacenados en el cache
    private void evictIfPresent(String cacheName, Long key) {
        var cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.evict(key);
        }
    }

    // Validamos lo que se trajo del request al front end
    private void validatePurchaseRequest(PurchaseRequestDTO requestDTO) {
        if (requestDTO == null) {
            throw new ResponseStatusException(BAD_REQUEST, "El cuerpo de la solicitud es obligatorio");
        }
        if (requestDTO.clientId() == null) {
            throw new ResponseStatusException(BAD_REQUEST, "clientId es obligatorio");
        }
        if (requestDTO.items() == null || requestDTO.items().isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "La compra debe tener al menos un producto");
        }
        if (requestDTO.userId() != null && requestDTO.userId().size() > requestDTO.items().size()) {
            throw new ResponseStatusException(BAD_REQUEST, "¡UPS!, La cantidad de productos agregados a la venta" +
                    " deben de ser igual a la cantidad de dueños del producto");
        }
    }

    private Map<Long, Integer> groupRequestedQuantities(List<PurchaseItemRequestDTO> items) {
        Map<Long, Integer> quantities = new LinkedHashMap<>();

        for (PurchaseItemRequestDTO item : items) {
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

    private void validateProductForPurchase(Product product, Integer quantity) {
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

    private SalesItem buildSaleItem(Sale sale, UserClient client, Product product, Integer quantity, LocalDateTime date) {
        SalesItem salesItem = new SalesItem();
        salesItem.setSales(sale);
        salesItem.setUserClient(client);
        salesItem.setProduct(product);
        salesItem.setQuantity(quantity);
        salesItem.setState(Status.COMPLETED);
        salesItem.setDate(date);
        return salesItem;
    }

    // Creamos un funcion para armar la repuesta al front end
    private PurchaseItemResponseDTO toPurchaseItemResponse(SalesItem salesItem) {
        BigDecimal unitPrice = unitPrice(salesItem.getProduct());
        BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(salesItem.getQuantity()));

        return new PurchaseItemResponseDTO(
                salesItem.getProduct().getId(),
                salesItem.getProduct().getName(),
                salesItem.getQuantity(),
                unitPrice,
                subtotal
        );
    }

    // Creamos una funcion para cambiar la dimension de almacenamiento de un dato decimal
    private BigDecimal unitPrice(Product product) {
        return BigDecimal.valueOf(product.getPrice());
    }

    private record SaleDraft(
            Product product,
            Integer quantity,
            BigDecimal subtotal,
            Long adminId
    ) {
    }

    @Transactional
    public ProductResponseDTO makeSale(Product productRequest, UserClient userClient, Integer amount) {
        Product product = productRepository.findById(productRequest.getId())
                .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "Product not found"));

        if (amount > product.getStock()) {
            throw new ResponseStatusException(CONFLICT, "There is no enough stock");
        }

        product.setStock(product.getStock() - amount);
        productRepository.save(product);

        return ProductResponseDTO.fromEntity(product);
    }
}
