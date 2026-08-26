package com.apiproject.DTOs.General;

import com.apiproject.repositories.projection.ShProductCardProjection;

import java.time.LocalDateTime;

public record ShProductCardResponseDTO(
        Long id,
        String name,
        Double price,
        Integer stock,
        String category,
        String description,
        boolean active,
        LocalDateTime timeOfUse,
        Long levelOfSecondHandProduct,
        String ownerName,
        String imageUrl
) {
    public static ShProductCardResponseDTO fromProjection(ShProductCardProjection projection) {
        return new ShProductCardResponseDTO(
                projection.getId(),
                projection.getName(),
                projection.getPrice(),
                projection.getStock(),
                projection.getCategory(),
                projection.getDescription(),
                Boolean.TRUE.equals(projection.getActive()),
                projection.getTimeOfUse(),
                projection.getLevelOfSecondHandProduct(),
                projection.getOwnerName(),
                projection.getImageUrl()
        );
    }
}
