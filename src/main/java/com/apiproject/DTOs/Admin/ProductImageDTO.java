package com.apiproject.DTOs.Admin;

import com.apiproject.entities.admin.ProductImage;

public record ProductImageDTO(
        Long id,
        String fileName,
        Long displayOrder,
        String url) {

    public static ProductImageDTO fromEntity(ProductImage productImage) {
        return new ProductImageDTO(
                productImage.getId(),
                productImage.getFileName(),
                productImage.getDisplayOrder(),
                productImage.getUrl());
    }
}
