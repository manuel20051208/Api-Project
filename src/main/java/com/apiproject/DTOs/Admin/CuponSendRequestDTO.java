package com.apiproject.DTOs.Admin;

/**
 * Request del boton "enviar cupon": el admin selecciona un cliente y le envia
 * un cupon ya creado. {@code clientId} es el destinatario y {@code cuponId} el cupon.
 */
public record CuponSendRequestDTO(
        Long clientId,
        Long cuponId,
        Integer usageLimit
) {
}