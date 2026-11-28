package com.apiproject.DTOs.General;

import java.util.List;

/**
 * Cuerpo opcional de la cancelacion: itemIds null o vacio = cancelar toda la venta;
 * con ids = cancelacion parcial de esos items.
 */
public record CancelSaleRequestDTO(List<Long> itemIds) {
}
