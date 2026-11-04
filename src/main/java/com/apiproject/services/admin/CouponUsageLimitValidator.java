package com.apiproject.services.admin;

import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * Regla de negocio compartida por los cupones normales, de segunda mano y de servicios:
 * el limite de usos por cliente ({@code usage_limit} de la asignacion) no puede superar
 * el {@code quantity} definido al crear el cupon (usos totales del cupon).
 *
 * <p>{@code quantity == null} significa ilimitado, por lo que no se aplica tope.</p>
 */
public final class CouponUsageLimitValidator {

    private CouponUsageLimitValidator() {
    }

    /**
     * Valida que el usageLimit pedido para una asignacion no exceda los usos del cupon.
     *
     * @param couponQuantity usos definidos en el cupon (null = ilimitado)
     * @param usageLimit     limite de usos que se quiere dar al cliente
     * @throws ResponseStatusException 400 si el limite del cliente supera el del cupon
     */
    public static void validate(Integer couponQuantity, Integer usageLimit) {
        if (couponQuantity == null || usageLimit == null) {
            return; // cupon ilimitado (o request sin limite, que se valida aparte)
        }
        if (usageLimit > couponQuantity) {
            throw new ResponseStatusException(BAD_REQUEST,
                    "usageLimit (" + usageLimit + ") no puede superar los usos definidos en el cupon (quantity="
                            + couponQuantity + ")");
        }
    }
}
