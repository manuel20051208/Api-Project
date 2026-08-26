package com.apiproject.DTOs.Admin;

import java.time.LocalDateTime;

public record ServiceCuponRequestDTO(
        String serviceCuponCode,
        LocalDateTime cuponDateLimit,
        Double discount,
        Integer quantity
) {
}
