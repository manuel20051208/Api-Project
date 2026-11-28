package com.apiproject.DTOs.Admin;

import java.math.BigDecimal;

import java.time.LocalDateTime;

public record ServiceCuponRequestDTO(
        String serviceCuponCode,
        LocalDateTime cuponDateLimit,
        BigDecimal discount,
        Integer quantity
) {
}
