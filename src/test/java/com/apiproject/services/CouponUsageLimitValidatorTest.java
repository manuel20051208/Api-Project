package com.apiproject.services;

import com.apiproject.services.admin.CouponUsageLimitValidator;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CouponUsageLimitValidatorTest {

    @Test
    void permiteLimiteIgualAlQuantityDelCupon() {
        assertDoesNotThrow(() -> CouponUsageLimitValidator.validate(5, 5));
    }

    @Test
    void permiteLimiteMenorAlQuantityDelCupon() {
        assertDoesNotThrow(() -> CouponUsageLimitValidator.validate(5, 1));
    }

    @Test
    void rechazaLimiteMayorAlQuantityDelCupon() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> CouponUsageLimitValidator.validate(3, 4));

        assertEquals(400, ex.getStatusCode().value());
        assertEquals("usageLimit (4) no puede superar los usos definidos en el cupon (quantity=3)",
                ex.getReason());
    }

    @Test
    void sinTopeCuandoElCuponEsIlimitado() {
        assertDoesNotThrow(() -> CouponUsageLimitValidator.validate(null, 999));
    }

    @Test
    void sinTopeCuandoNoSeInformaUsageLimit() {
        assertDoesNotThrow(() -> CouponUsageLimitValidator.validate(2, null));
    }
}
