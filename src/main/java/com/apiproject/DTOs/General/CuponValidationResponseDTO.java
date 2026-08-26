package com.apiproject.DTOs.General;

import java.math.BigDecimal;

public record CuponValidationResponseDTO(
        boolean valid,
        String message,
        BigDecimal discount,
        Long cuponId
) {
    public static CuponValidationResponseDTO invalid(String message) {
        return new CuponValidationResponseDTO(false, message, null, null);
    }
}
