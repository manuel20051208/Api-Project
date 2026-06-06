package com.example.apiproject.DTOs.Admin;

import com.example.apiproject.entities.general.SalesItem;
import com.example.apiproject.enums.Status;

import java.time.LocalDateTime;

public record SaleItemResponseDTO(
        Long id,
        String name,
        String productName,
        Integer quantity,
        Status status,
        LocalDateTime date
) {
    public static SaleItemResponseDTO fromEntity(SalesItem salesItem) {
        return new SaleItemResponseDTO(
                salesItem.getId(),
                salesItem.getUserClient().getFullName(),
                salesItem.getProduct().getName(),
                salesItem.getQuantity(),
                salesItem.getState(),
                salesItem.getDate());
    }
}