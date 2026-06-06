package com.example.apiproject.DTOs.Admin;

import com.example.apiproject.entities.admin.ProductImage;

public record ProductImageDTO(
        Long id,
        String fileName,
        String filePath,
        Long displayOrder,
        String url) {
    private static final String PUBLIC_BASE = "/uploads/products/";

    public static ProductImageDTO fromEntity(ProductImage productImage) {
        String path = productImage.getFilePath();
        return new ProductImageDTO(
                productImage.getId(),
                productImage.getFileName(),
                path,
                productImage.getDisplayOrder(),
                path == null || path.isBlank() ? null : PUBLIC_BASE + path);
    }
}