package com.apiproject.DTOs.General;

import com.apiproject.enums.SaleStatus;

import java.math.BigDecimal;
import java.util.List;

/**
 * Resultado de una cancelacion. refundedNow = reembolso de esta operacion, refundedTotal = acumulado de la venta,
 * totalAmount = lo cobrado originalmente y netAmount = totalAmount - refundedTotal.
 */
public record CancelSaleResponseDTO(
        Long saleId,
        SaleStatus status,
        BigDecimal refundedNow,
        BigDecimal refundedTotal,
        BigDecimal totalAmount,
        BigDecimal netAmount,
        List<Long> cancelledItemIds
) {
}
