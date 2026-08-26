package com.apiproject.DTOs.General;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ShPurchaseResponseDTO(
        List<Long> saleIds,
        Long clientId,
        String cuponCode,
        BigDecimal originalTotal,
        BigDecimal discountApplied,
        BigDecimal totalAmount,
        LocalDateTime createdAt,
        List<ShPurchaseItemResponseDTO> items
) {
}
