package com.example.apiproject.DTOs.General;

import java.math.BigDecimal;

public record PurchaseItemResponseDTO(
        Long productId,
        String productName,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal
) {
}
