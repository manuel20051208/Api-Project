package com.example.apiproject.DTOs.General;

import com.example.apiproject.DTOs.Admin.ProductImageDTO;
import com.example.apiproject.entities.general.Product;

import java.util.List;

public record ProductResponseDTO(
        Long id,
        String name,
        Double price,
        Integer stock,
        String category,
        String description,
        boolean active,
        Long userAdminId,
        List<ProductImageDTO> images
) {
    public static ProductResponseDTO fromEntity(Product product){
        return new ProductResponseDTO(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getStock(),
                product.getCategory(),
                product.getDescription(),
                product.isActive(),
                product.getUserAdmin() != null ? product.getUserAdmin().getId() : null,
                product.getImages().stream()
                        .map(ProductImageDTO::fromEntity)
                        .toList()
        );
    }
}