package com.apiproject.DTOs.Admin;

import java.math.BigDecimal;

import java.time.LocalDateTime;
import java.util.List;

public record CuponRequestDTO(
        String cuponCode,
        LocalDateTime cuponDateLimit,
        BigDecimal discount,
        Integer quantity,
        List<Long> productIds
) {
}
