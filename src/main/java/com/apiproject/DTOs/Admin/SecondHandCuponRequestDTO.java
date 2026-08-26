package com.apiproject.DTOs.Admin;

import java.time.LocalDateTime;
import java.util.List;

public record SecondHandCuponRequestDTO(
        String shCuponCode,
        LocalDateTime cuponDateLimit,
        Double discount,
        Integer quantity,
        List<Long> shProductIds
) {
}
