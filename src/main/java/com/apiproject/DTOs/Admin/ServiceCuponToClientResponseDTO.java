package com.apiproject.DTOs.Admin;

import java.math.BigDecimal;

import java.time.LocalDateTime;

public record ServiceCuponToClientResponseDTO(
        Long id,
        Long clientId,
        String clientName,
        String clientEmail,
        Long cuponId,
        String cuponCode,
        BigDecimal discount,
        LocalDateTime cuponDateLimit,
        Long serviceId,
        String serviceName,
        Integer usageLimit
) {
}
