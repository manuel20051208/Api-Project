package com.apiproject.DTOs.General;

import com.apiproject.DTOs.Admin.ShProductImageDTO;
import com.apiproject.entities.general.SecondHandProduct;

import java.time.LocalDateTime;
import java.util.List;

public record ShProductResponseDTO(
        Long id,
        String name,
        Double price,
        Integer stock,
        String category,
        String description,
        boolean active,
        LocalDateTime timeOfUse,
        Long levelOfSecondHandProduct,
        Long userAdminId,
        List<ShProductImageDTO> images
) {
    public static ShProductResponseDTO fromEntity(SecondHandProduct product) {
        return new ShProductResponseDTO(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getStock(),
                product.getCategory(),
                product.getDescription(),
                product.isActive(),
                product.getTimeOfUse(),
                product.getLevelOfSecondHandProduct(),
                product.getUserAdmin() != null ? product.getUserAdmin().getId() : null,
                product.getImages().stream()
                        .map(ShProductImageDTO::fromEntity)
                        .toList()
        );
    }
}
