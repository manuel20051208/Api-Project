package com.example.apiproject.DTOs.General;

import java.util.List;

public record PurchaseRequestDTO(
        Long clientId,
        List<Long> userId,
        List<PurchaseItemRequestDTO> items
) {
}