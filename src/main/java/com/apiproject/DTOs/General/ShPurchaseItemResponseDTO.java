package com.apiproject.DTOs.General;

import java.math.BigDecimal;

public record ShPurchaseItemResponseDTO(
        Long productId,
        String productName,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal
) {
}
