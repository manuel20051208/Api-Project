package com.apiproject.DTOs.Admin;

public record ServiceRequestDTO(
        String nameOfService,
        Double valueOfService,
        String descriptionOfService
) {
}
