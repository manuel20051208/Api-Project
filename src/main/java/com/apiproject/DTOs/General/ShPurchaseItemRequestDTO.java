package com.apiproject.DTOs.General;

public record ShPurchaseItemRequestDTO(
        Long productId,
        Integer quantity
) {
}
