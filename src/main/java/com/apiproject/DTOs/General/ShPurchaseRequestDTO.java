package com.apiproject.DTOs.General;

import java.util.List;

public record ShPurchaseRequestDTO(
        Long clientId,
        String cuponCode,
        List<ShPurchaseItemRequestDTO> items
) {
}
