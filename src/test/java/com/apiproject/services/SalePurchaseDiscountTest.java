package com.apiproject.services;

import com.apiproject.DTOs.General.PurchaseItemRequestDTO;
import com.apiproject.DTOs.General.PurchaseItemResponseDTO;
import com.apiproject.DTOs.General.PurchaseRequestDTO;
import com.apiproject.DTOs.General.PurchaseResponseDTO;
import com.apiproject.entities.admin.Cupon;
import com.apiproject.entities.admin.UserAdmin;
import com.apiproject.entities.client.UserClient;
import com.apiproject.entities.general.Product;
import com.apiproject.entities.general.Sale;
import com.apiproject.entities.general.SalesItem;
import com.apiproject.repositories.admin.CuponRepository;
import com.apiproject.repositories.client.ClientRepository;
import com.apiproject.repositories.client.PaymentCardRepository;
import com.apiproject.repositories.general.ProductRepository;
import com.apiproject.repositories.general.SaleItemRepository;
import com.apiproject.repositories.general.SaleRepository;
import com.apiproject.services.admin.CuponService;
import com.apiproject.services.general.ProductService;
import com.apiproject.services.general.SaleService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/** La compra guarda precio historico y reparte el descuento del cupon por item sin perder centavos. */
@ExtendWith(MockitoExtension.class)
class SalePurchaseDiscountTest {

    private static final long CLIENT_ID = 5L;
    private static final long ADMIN_ID = 7L;

    @Mock private SaleItemRepository saleItemRepository;
    @Mock private ProductRepository productRepository;
    @Mock private ClientRepository clientRepository;
    @Mock private PaymentCardRepository paymentCardRepository;
    @Mock private CacheManager cacheManager;
    @Mock private SaleRepository saleRepository;
    @Mock private CuponService cuponService;
    @Mock private CuponRepository cuponRepository;
    @Mock private ProductService productService;

    @InjectMocks private SaleService saleService;

    private static Product product(long id, String price) {
        UserAdmin admin = new UserAdmin();
        admin.setId(ADMIN_ID);
        Product p = new Product();
        p.setId(id);
        p.setName("P" + id);
        p.setPrice(new BigDecimal(price));
        p.setStock(10);
        p.setActive(true);
        p.setUserAdmin(admin);
        return p;
    }

    @Test
    @SuppressWarnings("unchecked")
    void descuentoPorItem_sumaExactamenteElDescuentoDeLaCompra() {
        // 7 % sobre 10.01 / 20.03 / 30.07 no divide exacto: 0.70 + 1.40 + 2.10 = 4.20 (redondeo HALF_UP por item)
        List<Product> products = List.of(product(1L, "10.01"), product(2L, "20.03"), product(3L, "30.07"));
        UserClient client = new UserClient();
        client.setId(CLIENT_ID);
        Cupon cupon = new Cupon();
        cupon.setId(9L);
        cupon.setCuponCode("SALE7");
        cupon.setDiscount(new BigDecimal("7.00"));
        cupon.setCuponDateLimit(LocalDateTime.now().plusDays(1));

        when(clientRepository.findById(CLIENT_ID)).thenReturn(Optional.of(client));
        when(paymentCardRepository.existsByUserClientIdAndActiveTrue(CLIENT_ID)).thenReturn(true);
        when(productRepository.findAllByIdInForUpdate(anyList())).thenReturn(products);
        when(cuponService.resolveForCart(eq("SALE7"), eq(CLIENT_ID), anyList(), any()))
                .thenReturn(new CuponService.CouponResolution(9L, new BigDecimal("7.00"), ADMIN_ID, List.of(1L, 2L, 3L)));
        when(cuponRepository.getReferenceById(9L)).thenReturn(cupon);
        when(saleRepository.saveAll(anyList())).thenAnswer(inv -> {
            List<Sale> sales = inv.getArgument(0);
            long id = 100;
            for (Sale s : sales) s.setId(id++);
            return sales;
        });

        PurchaseResponseDTO response = saleService.purchase(new PurchaseRequestDTO(CLIENT_ID, null, List.of(
                new PurchaseItemRequestDTO(1L, 1),
                new PurchaseItemRequestDTO(2L, 1),
                new PurchaseItemRequestDTO(3L, 1)), "SALE7"), CLIENT_ID);

        ArgumentCaptor<List<SalesItem>> captor = ArgumentCaptor.forClass(List.class);
        verify(saleItemRepository).saveAll(captor.capture());
        List<SalesItem> items = captor.getValue();

        BigDecimal itemsDiscount = items.stream().map(SalesItem::getDiscount).reduce(BigDecimal.ZERO, BigDecimal::add);
        assertEquals(new BigDecimal("4.20"), itemsDiscount);
        assertEquals(0, itemsDiscount.compareTo(response.discountApplied()),
                "la suma de descuentos por item == descuento de la compra");

        // precio historico guardado en el item
        assertEquals(new BigDecimal("10.01"), items.get(0).getUnitPrice());
        assertEquals(new BigDecimal("30.07"), items.get(2).getUnitPrice());

        // subtotal bruto y discount por item en la respuesta: sum(subtotal - discount) == totalAmount
        BigDecimal net = response.items().stream()
                .map((PurchaseItemResponseDTO i) -> i.subtotal().subtract(i.discount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        assertEquals(0, net.compareTo(response.totalAmount()));
        assertEquals(new BigDecimal("55.91"), response.totalAmount()); // 60.11 - 4.20
    }

    @Test
    @SuppressWarnings("unchecked")
    void sinCupon_elDescuentoDelItemEsCero() {
        UserClient client = new UserClient();
        client.setId(CLIENT_ID);
        when(clientRepository.findById(CLIENT_ID)).thenReturn(Optional.of(client));
        when(paymentCardRepository.existsByUserClientIdAndActiveTrue(CLIENT_ID)).thenReturn(true);
        when(productRepository.findAllByIdInForUpdate(anyList())).thenReturn(List.of(product(1L, "12.50")));
        when(saleRepository.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));

        PurchaseResponseDTO response = saleService.purchase(new PurchaseRequestDTO(CLIENT_ID, null,
                List.of(new PurchaseItemRequestDTO(1L, 2)), null), CLIENT_ID);

        ArgumentCaptor<List<SalesItem>> captor = ArgumentCaptor.forClass(List.class);
        verify(saleItemRepository).saveAll(captor.capture());
        assertEquals(0, BigDecimal.ZERO.compareTo(captor.getValue().getFirst().getDiscount()));
        assertEquals(new BigDecimal("25.00"), response.totalAmount());
        verify(cuponService, never()).redeem(any());
        verify(cuponService, never()).resolveForCart(any(), anyLong(), anyList(), any());
    }
}
