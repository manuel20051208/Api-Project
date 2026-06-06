package com.example.apiproject.DTOs.General;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PurchaseResponseDTO(
        Long saleId,
        Long clientId,
        BigDecimal totalAmount,
        LocalDateTime createdAt,
        List<PurchaseItemResponseDTO> items
) {
}
