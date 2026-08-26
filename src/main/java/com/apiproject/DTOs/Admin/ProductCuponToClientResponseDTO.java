package com.apiproject.DTOs.Admin;

import java.time.LocalDateTime;

public record ProductCuponToClientResponseDTO(
        Long id,
        Long clientId,
        String clientName,
        String clientEmail,
        Long cuponId,
        String cuponCode,
        Double discount,
        LocalDateTime cuponDateLimit,
        Long productId,
        String productName
) {
}
