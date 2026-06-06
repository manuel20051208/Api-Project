package com.example.apiproject.DTOs.General;

public record PurchaseItemRequestDTO(
        Long productId,
        Integer quantity
) {
}
