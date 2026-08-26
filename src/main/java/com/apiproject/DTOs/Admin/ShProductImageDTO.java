package com.apiproject.DTOs.Admin;

import com.apiproject.entities.admin.SecondHandProductImage;

public record ShProductImageDTO(
        Long id,
        String fileName,
        Long displayOrder,
        String url
) {
    public static ShProductImageDTO fromEntity(SecondHandProductImage image) {
        return new ShProductImageDTO(
                image.getId(),
                image.getFileName(),
                image.getDisplayOrder(),
                image.getUrl());
    }
}
