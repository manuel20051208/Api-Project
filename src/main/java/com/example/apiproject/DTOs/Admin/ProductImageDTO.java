package com.example.apiproject.DTOs.Admin;

import com.example.apiproject.entities.admin.ProductImage;

public record ProductImageDTO(
        Long id,
        String fileName,
        String filePath,
        Long displayOrder
) {
    public static ProductImageDTO fromEntity(ProductImage productImage){
        return new ProductImageDTO(
                productImage.getId(),
                productImage.getFileName(),
                productImage.getFilePath(),
                productImage.getDisplayOrder()
        );
    }
}