package com.example.apiproject.services.general;

import com.example.apiproject.DTOs.Admin.NotificationEventDTO;
import com.example.apiproject.DTOs.General.*;
import com.example.apiproject.config.CacheConstants;
import com.example.apiproject.entities.client.UserClient;
import com.example.apiproject.entities.general.Product;
import com.example.apiproject.entities.general.Sale;
import com.example.apiproject.entities.general.SalesItem;
import com.example.apiproject.enums.Status;
import com.example.apiproject.exceptions.ResourceNotFoundException;
import com.example.apiproject.repositories.client.ClientRepository;
import com.example.apiproject.repositories.client.PaymentCardRepository;
import com.example.apiproject.repositories.general.ProductRepository;
import com.example.apiproject.repositories.general.SaleItemRepository;
import com.example.apiproject.repositories.general.SaleRepository;
import com.example.apiproject.services.user.admin.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
public class SaleService {
    private final SaleRepository saleRepository;
    private final NotificationService notificationService;
    private final SaleItemRepository saleItemRepository;
    private final ProductRepository productRepository;
    private final ClientRepository clientRepository;
    private final PaymentCardRepository paymentCardRepository;
    private final CacheManager cacheManager;

    @Transactional
    @Caching(
            put = {
                    @CachePut(value = "dashboard", key = "#authenticatedClientId"),
                    @CachePut(value = "ClientHistory", key = "#authenticatedClientId")
            }
    )
    public PurchaseResponseDTO purchase(PurchaseRequestDTO requestDTO, Long authenticatedClientId) {
        // Validamos el cuerpo del request de la venta
        validatePurchaseRequest(requestDTO);

        // Validamos si hay clientes por ID en la base de datos
        if (!clientRepository.existsById(requestDTO.clientId())) {
            throw new ResourceNotFoundException("Client not found: " + requestDTO.clientId());
        }

        // Validamos que el cliente que hará la compra es igual al ID del cliente actual autenticado
        if (!requestDTO.clientId().equals(authenticatedClientId)) {
            throw new ResponseStatusException(FORBIDDEN, "No puedes comprar usando otro cliente");
        }

        // Validamos que al menos haya una tarjeta activa para realizar compra (tarjeta simulada)
        if (!paymentCardRepository.existsByUserClientIdAndActiveTrue(requestDTO.clientId())) {
            throw new ResponseStatusException(PAYMENT_REQUIRED, "El cliente no tiene una tarjeta activa");
        }

        // Luego de las validaciones anteriores buscamos el cliente
        UserClient client = clientRepository.findById(authenticatedClientId).get();


        Map<Long, Integer> requestedQuantities = groupRequestedQuantities(requestDTO.items());
        Map<Long, Product> productsById = productRepository.findAllByIdInForUpdate(
                requestedQuantities.keySet().stream().toList())
                .stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        if (productsById.size() != requestedQuantities.size()) {
            throw new ResourceNotFoundException("One or more products were not found");
        }

        LocalDateTime now = LocalDateTime.now();
        BigDecimal totalAmount = BigDecimal.ZERO;
        Long saleOwnerId = null;

        Sale sale = new Sale();
        sale.setUserClient(client);
        sale.setHora(now);

        // Los admins dueños de los productos
        List<Long> adminsId = requestDTO.userId();

        for (Map.Entry<Long, Integer> entry : requestedQuantities.entrySet()) {
            Product product = productsById.get(entry.getKey());
            int quantity = entry.getValue();

            if (!product.isActive()) {
                throw new ResponseStatusException(CONFLICT, "Product is not active: " + product.getId());
            }
            if (product.getStock() == null || product.getStock() < quantity) {
                throw new ResponseStatusException(CONFLICT, "Insufficient stock for product: " + product.getName());
            }

            product.setStock(product.getStock() - quantity);
            totalAmount = totalAmount.add(unitPrice(product).multiply(BigDecimal.valueOf(quantity)));

            sale.setUserAdmin(product.getUserAdmin());
            saleOwnerId = product.getUserAdmin().getId();
        }

        sale.setTotalAmount(totalAmount);
        Sale savedSale = saleRepository.save(sale);

        List<SalesItem> saleItems = requestedQuantities.entrySet().stream()
                .map(entry -> buildSaleItem(savedSale, client, productsById.get(entry.getKey()), entry.getValue(), now))
                .toList();

        saleItemRepository.saveAll(saleItems);

        List<PurchaseItemResponseDTO> responseItems = saleItems.stream()
                .map(this::toPurchaseItemResponse)
                .toList();

        notificationService.push(saleOwnerId, new NotificationEventDTO(
                "VENTA_NUEVA",
                "Nueva venta por $" + totalAmount,
                saleOwnerId
        ));

        for (Map.Entry<Long, Integer> entry : requestedQuantities.entrySet()) {
            Product product = productsById.get(entry.getKey());
            if (product.getStock() <= 5) {
                notificationService.push(saleOwnerId, new NotificationEventDTO(
                        "STOCK_BAJO",
                        "Stock bajo: " + product.getName() + " (" + product.getStock() + " restantes)",
                        saleOwnerId
                ));
            }
        }
        evictAdminCaches(saleOwnerId);

        return new PurchaseResponseDTO(savedSale.getId(), requestDTO.clientId(), totalAmount, now, responseItems);
    }

    private void evictAdminCaches(Long adminId) {
        if (adminId == null) return;
        evictIfPresent(CacheConstants.DASHBOARD, adminId);
        evictIfPresent(CacheConstants.DASHBOARD_CLIENTS, adminId);
        evictIfPresent(CacheConstants.CLIENTS, adminId);
    }

    private void evictIfPresent(String cacheName, Long key) {
        var cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.evict(key);
        }
    }

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
        if (requestDTO.userId().size() > requestDTO.items().size()) {
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

    private BigDecimal unitPrice(Product product) {
        return BigDecimal.valueOf(product.getPrice());
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
