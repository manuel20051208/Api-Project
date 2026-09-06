package com.apiproject.DTOs.General;

public record TheThreeBestProducts(
        Long productId,
        Long userId,
        String name,
        Long amountOfBuys
) {
}
