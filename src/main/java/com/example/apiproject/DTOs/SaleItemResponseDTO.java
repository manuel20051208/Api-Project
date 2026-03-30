package com.example.apiproject.DTOs;

import com.example.apiproject.entities.general.entities.SalesItem;
import com.example.apiproject.enums.Status;
import org.jspecify.annotations.NonNull;

import java.time.LocalDateTime;

public record SaleItemResponseDTO(
        Long id,
        String name,
        String productName,
        Integer quantity,
        Status status,
        LocalDateTime date
) {
    public static @NonNull SaleItemResponseDTO fromEntity(@NonNull SalesItem salesItem) {
        return new SaleItemResponseDTO(
                salesItem.getId(),
                salesItem.getUserClient().getFullName(),
                salesItem.getProduct().getName(),
                salesItem.getQuantity(),
                salesItem.getState(),
                salesItem.getDate());
    }
}