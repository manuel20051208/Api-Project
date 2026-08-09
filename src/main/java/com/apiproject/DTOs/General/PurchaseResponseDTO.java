package com.apiproject.DTOs.General;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PurchaseResponseDTO(
        Long saleId,
        List<Long> saleIds,
        Long clientId,
        BigDecimal totalAmount,
        LocalDateTime createdAt,
        List<PurchaseItemResponseDTO> items
) {
    public PurchaseResponseDTO(
            Long saleId,
            Long clientId,
            BigDecimal totalAmount,
            LocalDateTime createdAt,
            List<PurchaseItemResponseDTO> items
    ) {
        this(saleId, List.of(saleId), clientId, totalAmount, createdAt, items);
    }
}
