package com.example.apiproject.DTOs.General;

import com.example.apiproject.DTOs.Admin.ProductImageDTO;
import com.example.apiproject.entities.admin.ProductImage;
import com.example.apiproject.entities.general.entities.Product;
import org.jspecify.annotations.NonNull;

import java.util.List;

public record ProductResponseDTO(
        Long id,
        String name,
        Double price,
        Integer stock,
        String category,
        boolean active,
        List<ProductImageDTO> images
) {
    public static ProductResponseDTO fromEntity(@NonNull Product product){
        return new ProductResponseDTO(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getStock(),
                product.getCategory(),
                product.isActive(),
                product.getImages().stream()
                        .map(ProductImageDTO::fromEntity)
                        .toList()
        );
    }
}