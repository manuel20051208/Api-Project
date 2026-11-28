package com.apiproject.DTOs.Admin;

import java.math.BigDecimal;

public record ServiceRequestDTO(
        String nameOfService,
        BigDecimal valueOfService,
        String descriptionOfService
) {
}
