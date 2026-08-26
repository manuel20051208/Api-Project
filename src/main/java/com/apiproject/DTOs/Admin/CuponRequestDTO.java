package com.apiproject.DTOs.Admin;

import java.time.LocalDateTime;
import java.util.List;

public record CuponRequestDTO(
        String cuponCode,
        LocalDateTime cuponDateLimit,
        Double discount,
        Integer quantity,
        List<Long> productIds
) {
}
