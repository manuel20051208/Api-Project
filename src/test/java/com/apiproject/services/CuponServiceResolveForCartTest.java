package com.apiproject.services;

import com.apiproject.entities.admin.Cupon;
import com.apiproject.entities.admin.UserAdmin;
import com.apiproject.repositories.admin.CuponRepository;
import com.apiproject.repositories.admin.CuponUsedByClientsRepository;
import com.apiproject.repositories.admin.ProductCuponRepository;
import com.apiproject.repositories.admin.ProductCuponToAClientRepository;
import com.apiproject.services.admin.CuponService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Modelo hibrido de cupones en resolveForCart:
 * sin asignaciones = publico (1 uso por cliente); con asignaciones = exclusivo de los asignados.
 */
@ExtendWith(MockitoExtension.class)
class CuponServiceResolveForCartTest {

    private static final String CODE = "PROMO10";
    private static final long CUPON_ID = 7L;
    private static final long ADMIN_ID = 1L;
    private static final long CLIENT_ID = 50L;
    private static final long PRODUCT_ID = 100L;
    private static final Function<Long, Long> OWNER_FN = productId -> ADMIN_ID;

    @Mock private CuponRepository cuponRepository;
    @Mock private ProductCuponRepository productCuponRepository;
    @Mock private CuponUsedByClientsRepository cuponUsedByClientsRepository;
    @Mock private ProductCuponToAClientRepository assignmentRepository;

    @InjectMocks private CuponService cuponService;

    @BeforeEach
    void cuponVigenteVinculadoAlProducto() {
        UserAdmin owner = UserAdmin.builder().id(ADMIN_ID).build();
        Cupon cupon = new Cupon();
        cupon.setId(CUPON_ID);
        cupon.setCuponCode(CODE);
        cupon.setCuponDateLimit(LocalDateTime.now().plusDays(5));
        cupon.setDiscount(10.0);
        cupon.setQuantity(null);
        cupon.setUserAdmin(owner);

        when(cuponRepository.lockByCode(CODE)).thenReturn(Optional.of(cupon));
        when(productCuponRepository.findProductIdsByCuponId(CUPON_ID)).thenReturn(List.of(PRODUCT_ID));
    }

    private CuponService.CouponResolution resolve() {
        return cuponService.resolveForCart(CODE, CLIENT_ID, List.of(PRODUCT_ID), OWNER_FN);
    }

    private static HttpStatus statusOf(ResponseStatusException ex) {
        return HttpStatus.valueOf(ex.getStatusCode().value());
    }

    @Test
    void publico_primerUso_aplica() {
        when(cuponUsedByClientsRepository.countByCuponIdAndClientId(CUPON_ID, CLIENT_ID)).thenReturn(0L);
        when(assignmentRepository.existsAnyAssignment(CUPON_ID)).thenReturn(false);

        CuponService.CouponResolution resolution = resolve();

        assertEquals(CUPON_ID, resolution.cuponId());
        assertEquals(List.of(PRODUCT_ID), resolution.elegibleProductIds());
    }

    @Test
    void publico_segundoUso_409() {
        when(cuponUsedByClientsRepository.countByCuponIdAndClientId(CUPON_ID, CLIENT_ID)).thenReturn(1L);
        when(assignmentRepository.existsAnyAssignment(CUPON_ID)).thenReturn(false);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, this::resolve);
        assertEquals(HttpStatus.CONFLICT, statusOf(ex));
    }

    @Test
    void exclusivo_clienteNoAsignado_403() {
        when(cuponUsedByClientsRepository.countByCuponIdAndClientId(CUPON_ID, CLIENT_ID)).thenReturn(0L);
        when(assignmentRepository.existsAnyAssignment(CUPON_ID)).thenReturn(true);
        when(assignmentRepository.existsAssignment(CLIENT_ID, CUPON_ID)).thenReturn(false);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, this::resolve);
        assertEquals(HttpStatus.FORBIDDEN, statusOf(ex));
    }

    @Test
    void exclusivo_asignadoSinLimite_aplicaAunqueYaLoHayaUsado() {
        when(cuponUsedByClientsRepository.countByCuponIdAndClientId(CUPON_ID, CLIENT_ID)).thenReturn(5L);
        when(assignmentRepository.existsAnyAssignment(CUPON_ID)).thenReturn(true);
        when(assignmentRepository.existsAssignment(CLIENT_ID, CUPON_ID)).thenReturn(true);
        when(assignmentRepository.findUsageLimitByClientAndCupon(CLIENT_ID, CUPON_ID)).thenReturn(Optional.empty());

        assertDoesNotThrow(this::resolve);
    }

    @Test
    void exclusivo_asignadoConLimiteAgotado_409() {
        when(cuponUsedByClientsRepository.countByCuponIdAndClientId(CUPON_ID, CLIENT_ID)).thenReturn(2L);
        when(assignmentRepository.existsAnyAssignment(CUPON_ID)).thenReturn(true);
        when(assignmentRepository.existsAssignment(CLIENT_ID, CUPON_ID)).thenReturn(true);
        when(assignmentRepository.findUsageLimitByClientAndCupon(CLIENT_ID, CUPON_ID)).thenReturn(Optional.of(2));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, this::resolve);
        assertEquals(HttpStatus.CONFLICT, statusOf(ex));
    }
}
