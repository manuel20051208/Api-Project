package com.apiproject.services;

import com.apiproject.DTOs.General.CancelSaleRequestDTO;
import com.apiproject.DTOs.General.CancelSaleResponseDTO;
import com.apiproject.entities.admin.Cupon;
import com.apiproject.entities.admin.SecondHandCupon;
import com.apiproject.entities.admin.UserAdmin;
import com.apiproject.entities.client.UserClient;
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
import com.apiproject.services.general.ProductService;
import com.apiproject.services.general.SaleCancellationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/** Cancelacion total/parcial de ventas: permisos, stock, reembolso y restitucion del cupon. */
@ExtendWith(MockitoExtension.class)
class SaleCancellationTest {

    private static final long ADMIN_ID = 7L;
    private static final long CLIENT_ID = 5L;
    private static final long SALE_ID = 100L;
    private static final long CUPON_ID = 9L;

    private static final AuthenticatedUser ADMIN = AuthenticatedUser.fromToken(ADMIN_ID, "admin@x.com", "ADMIN");
    private static final AuthenticatedUser OWNER_CLIENT = AuthenticatedUser.fromToken(CLIENT_ID, "c@x.com", "CLIENT");
    private static final AuthenticatedUser OTHER_CLIENT = AuthenticatedUser.fromToken(99L, "o@x.com", "CLIENT");
    private static final AuthenticatedUser OTHER_ADMIN = AuthenticatedUser.fromToken(88L, "a2@x.com", "ADMIN");

    @Mock private SaleRepository saleRepository;
    @Mock private SaleItemRepository saleItemRepository;
    @Mock private ProductRepository productRepository;
    @Mock private ProductService productService;
    @Mock private CuponService cuponService;
    @Mock private ShSaleRepository shSaleRepository;
    @Mock private ShSaleItemRepository shSaleItemRepository;
    @Mock private SecondHandProductRepository secondHandProductRepository;
    @Mock private SecondHandCuponService secondHandCuponService;

    @InjectMocks private SaleCancellationService service;

    // ---------- helpers ----------

    private static Sale sale(LocalDateTime createdAt, boolean withCupon, String total) {
        Sale sale = new Sale();
        sale.setId(SALE_ID);
        UserAdmin admin = new UserAdmin();
        admin.setId(ADMIN_ID);
        sale.setUserAdmin(admin);
        UserClient client = new UserClient();
        client.setId(CLIENT_ID);
        sale.setUserClient(client);
        sale.setHora(createdAt);
        sale.setTotalAmount(new BigDecimal(total));
        if (withCupon) {
            Cupon cupon = new Cupon();
            cupon.setId(CUPON_ID);
            sale.setCupon(cupon);
        }
        return sale;
    }

    private static Product product(long id, int stock) {
        Product p = new Product();
        p.setId(id);
        p.setStock(stock);
        p.setPrice(new BigDecimal("10.00"));
        return p;
    }

    private static SalesItem item(long id, Product product, int qty, String unitPrice, String discount) {
        SalesItem item = new SalesItem();
        item.setId(id);
        item.setProduct(product);
        item.setQuantity(qty);
        item.setUnitPrice(new BigDecimal(unitPrice));
        item.setDiscount(new BigDecimal(discount));
        item.setState(Status.COMPLETED);
        return item;
    }

    private void stubSaleAndItems(Sale sale, List<SalesItem> items, Product... products) {
        when(saleRepository.findByIdForUpdate(SALE_ID)).thenReturn(Optional.of(sale));
        when(saleItemRepository.findBySalesId(SALE_ID)).thenReturn(items);
        when(productRepository.findAllByIdInForUpdate(anyList())).thenReturn(List.of(products));
    }

    private static HttpStatus statusOf(ResponseStatusException ex) {
        return HttpStatus.valueOf(ex.getStatusCode().value());
    }

    // ---------- casos ----------

    @Test
    void adminCancelaVentaCompleta_devuelveStock_reembolsa_yRestituyeCupon() {
        Sale sale = sale(LocalDateTime.now().minusDays(10), true, "90.00"); // admin puede en cualquier momento
        Product product = product(1L, 3);
        SalesItem item = item(11L, product, 2, "50.00", "10.00"); // bruto 100 - descuento 10 = 90
        stubSaleAndItems(sale, List.of(item), product);

        CancelSaleResponseDTO response = service.cancelSale(SALE_ID, null, ADMIN);

        assertEquals(5, product.getStock(), "el stock vuelve (+quantity)");
        assertEquals(Status.CANCELLED, item.getState());
        assertNotNull(item.getCancelledAt());
        assertEquals(SaleStatus.CANCELLED, sale.getStatus());
        assertEquals(new BigDecimal("90.00"), sale.getRefundedAmount());
        assertEquals(new BigDecimal("90.00"), response.refundedNow());
        assertEquals(new BigDecimal("90.00"), response.refundedTotal());
        assertEquals(new BigDecimal("0.00"), response.netAmount());
        assertEquals(new BigDecimal("90.00"), response.totalAmount());
        assertEquals(List.of(11L), response.cancelledItemIds());
        verify(cuponService).restoreUsage(CUPON_ID, SALE_ID);
        verify(productService).refreshCachesAfterStockChange(any());
    }

    @Test
    void clienteDuenoDentroDe24h_puedeCancelar() {
        Sale sale = sale(LocalDateTime.now().minusHours(23), false, "100.00");
        Product product = product(1L, 0);
        SalesItem item = item(11L, product, 2, "50.00", "0.00");
        stubSaleAndItems(sale, List.of(item), product);

        CancelSaleResponseDTO response = service.cancelSale(SALE_ID, new CancelSaleRequestDTO(List.of()), OWNER_CLIENT);

        assertEquals(SaleStatus.CANCELLED, response.status());
        assertEquals(2, product.getStock());
        verify(cuponService, never()).restoreUsage(anyLong(), anyLong());
    }

    @Test
    void clienteDuenoDespuesDe24h_403() {
        Sale sale = sale(LocalDateTime.now().minusHours(25), false, "100.00");
        when(saleRepository.findByIdForUpdate(SALE_ID)).thenReturn(Optional.of(sale));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.cancelSale(SALE_ID, null, OWNER_CLIENT));

        assertEquals(HttpStatus.FORBIDDEN, statusOf(ex));
        assertTrue(ex.getReason().contains("24"), "el mensaje explica la ventana de 24 h");
        verifyNoInteractions(productRepository, saleItemRepository);
    }

    @Test
    void otroCliente_403() {
        Sale sale = sale(LocalDateTime.now().minusHours(1), false, "100.00");
        when(saleRepository.findByIdForUpdate(SALE_ID)).thenReturn(Optional.of(sale));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.cancelSale(SALE_ID, null, OTHER_CLIENT));

        assertEquals(HttpStatus.FORBIDDEN, statusOf(ex));
        verifyNoInteractions(productRepository, saleItemRepository);
    }

    @Test
    void otroAdmin_403() {
        Sale sale = sale(LocalDateTime.now().minusHours(1), false, "100.00");
        when(saleRepository.findByIdForUpdate(SALE_ID)).thenReturn(Optional.of(sale));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.cancelSale(SALE_ID, null, OTHER_ADMIN));

        assertEquals(HttpStatus.FORBIDDEN, statusOf(ex));
    }

    @Test
    void ventaInexistente_404() {
        when(saleRepository.findByIdForUpdate(SALE_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.cancelSale(SALE_ID, null, ADMIN));
    }

    @Test
    void cancelacionParcial_reembolsaSubtotalMenosDescuentoDelItem_yNoRestituyeCupon() {
        // total cobrado: A 20.00 + B (30.00 - 5.00) = 45.00
        Sale sale = sale(LocalDateTime.now().minusHours(1), true, "45.00");
        Product pa = product(1L, 10);
        Product pb = product(2L, 4);
        SalesItem a = item(11L, pa, 2, "10.00", "0.00");
        SalesItem b = item(12L, pb, 1, "30.00", "5.00");
        stubSaleAndItems(sale, List.of(a, b), pb);

        CancelSaleResponseDTO response = service.cancelSale(SALE_ID, new CancelSaleRequestDTO(List.of(12L)), ADMIN);

        assertEquals(SaleStatus.PARTIALLY_CANCELLED, response.status());
        assertEquals(new BigDecimal("25.00"), response.refundedNow(), "30.00 - 5.00 de descuento del item");
        assertEquals(new BigDecimal("20.00"), response.netAmount());
        assertEquals(List.of(12L), response.cancelledItemIds());
        assertEquals(5, pb.getStock());
        assertEquals(10, pa.getStock(), "el item no cancelado no toca su stock");
        assertEquals(Status.COMPLETED, a.getState());
        assertNull(a.getCancelledAt());
        verify(cuponService, never()).restoreUsage(anyLong(), anyLong());
    }

    @Test
    void cancelarElRestoDespuesDeUnaParcial_dejaLaVentaCancelada_yRestituyeCupon() {
        Sale sale = sale(LocalDateTime.now().minusHours(1), true, "45.00");
        sale.setStatus(SaleStatus.PARTIALLY_CANCELLED);
        sale.setRefundedAmount(new BigDecimal("25.00"));
        Product pa = product(1L, 10);
        Product pb = product(2L, 5);
        SalesItem a = item(11L, pa, 2, "10.00", "0.00");
        SalesItem b = item(12L, pb, 1, "30.00", "5.00");
        b.setState(Status.CANCELLED);
        b.setCancelledAt(LocalDateTime.now().minusMinutes(5));
        stubSaleAndItems(sale, List.of(a, b), pa);

        CancelSaleResponseDTO response = service.cancelSale(SALE_ID, null, ADMIN);

        assertEquals(SaleStatus.CANCELLED, response.status());
        assertEquals(new BigDecimal("20.00"), response.refundedNow());
        assertEquals(new BigDecimal("45.00"), response.refundedTotal());
        assertEquals(new BigDecimal("0.00"), response.netAmount());
        verify(cuponService).restoreUsage(CUPON_ID, SALE_ID);
    }

    @Test
    void itemYaCancelado_409() {
        Sale sale = sale(LocalDateTime.now().minusHours(1), false, "100.00");
        Product product = product(1L, 3);
        SalesItem item = item(11L, product, 2, "50.00", "0.00");
        item.setState(Status.CANCELLED);
        item.setCancelledAt(LocalDateTime.now().minusMinutes(1));
        when(saleRepository.findByIdForUpdate(SALE_ID)).thenReturn(Optional.of(sale));
        when(saleItemRepository.findBySalesId(SALE_ID)).thenReturn(List.of(item));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.cancelSale(SALE_ID, new CancelSaleRequestDTO(List.of(11L)), ADMIN));

        assertEquals(HttpStatus.CONFLICT, statusOf(ex));
        assertEquals(3, product.getStock(), "no se devuelve stock dos veces");
    }

    @Test
    void ventaYaCancelada_409() {
        Sale sale = sale(LocalDateTime.now().minusHours(1), false, "100.00");
        sale.setStatus(SaleStatus.CANCELLED);
        Product product = product(1L, 3);
        SalesItem item = item(11L, product, 2, "50.00", "0.00");
        item.setState(Status.CANCELLED);
        item.setCancelledAt(LocalDateTime.now().minusMinutes(1));
        when(saleRepository.findByIdForUpdate(SALE_ID)).thenReturn(Optional.of(sale));
        when(saleItemRepository.findBySalesId(SALE_ID)).thenReturn(List.of(item));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.cancelSale(SALE_ID, null, ADMIN));

        assertEquals(HttpStatus.CONFLICT, statusOf(ex));
    }

    @Test
    void itemQueNoEsDeLaVenta_400() {
        Sale sale = sale(LocalDateTime.now().minusHours(1), false, "100.00");
        SalesItem item = item(11L, product(1L, 3), 2, "50.00", "0.00");
        when(saleRepository.findByIdForUpdate(SALE_ID)).thenReturn(Optional.of(sale));
        when(saleItemRepository.findBySalesId(SALE_ID)).thenReturn(List.of(item));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.cancelSale(SALE_ID, new CancelSaleRequestDTO(List.of(999L)), ADMIN));

        assertEquals(HttpStatus.BAD_REQUEST, statusOf(ex));
    }

    // ---------- segunda mano ----------

    @Test
    void shAdminCancelaVentaCompleta_devuelveStock_yRestituyeCupon() {
        ShSale sale = new ShSale();
        sale.setId(SALE_ID);
        UserAdmin admin = new UserAdmin();
        admin.setId(ADMIN_ID);
        sale.setUserAdmin(admin);
        UserClient client = new UserClient();
        client.setId(CLIENT_ID);
        sale.setUserClient(client);
        sale.setHora(LocalDateTime.now().minusDays(3));
        sale.setTotalAmount(new BigDecimal("45.00"));
        SecondHandCupon cupon = new SecondHandCupon();
        cupon.setId(CUPON_ID);
        sale.setCupon(cupon);

        SecondHandProduct product = new SecondHandProduct();
        product.setId(3L);
        product.setStock(0);
        ShSalesItem item = new ShSalesItem();
        item.setId(31L);
        item.setShProduct(product);
        item.setQuantity(1);
        item.setUnitPrice(new BigDecimal("50.00"));
        item.setDiscount(new BigDecimal("5.00"));
        item.setState(Status.COMPLETED);

        when(shSaleRepository.findByIdForUpdate(SALE_ID)).thenReturn(Optional.of(sale));
        when(shSaleItemRepository.findByShSaleId(SALE_ID)).thenReturn(List.of(item));
        when(secondHandProductRepository.findAllByIdInForUpdate(anyList())).thenReturn(List.of(product));

        CancelSaleResponseDTO response = service.cancelShSale(SALE_ID, null, ADMIN);

        assertEquals(1, product.getStock());
        assertEquals(SaleStatus.CANCELLED, response.status());
        assertEquals(new BigDecimal("45.00"), response.refundedNow());
        verify(secondHandCuponService).restoreUsage(CUPON_ID, SALE_ID);
    }

    @Test
    void shClienteDespuesDe24h_403() {
        ShSale sale = new ShSale();
        sale.setId(SALE_ID);
        UserAdmin admin = new UserAdmin();
        admin.setId(ADMIN_ID);
        sale.setUserAdmin(admin);
        UserClient client = new UserClient();
        client.setId(CLIENT_ID);
        sale.setUserClient(client);
        sale.setHora(LocalDateTime.now().minusHours(30));
        when(shSaleRepository.findByIdForUpdate(SALE_ID)).thenReturn(Optional.of(sale));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.cancelShSale(SALE_ID, null, OWNER_CLIENT));

        assertEquals(HttpStatus.FORBIDDEN, statusOf(ex));
    }
}
