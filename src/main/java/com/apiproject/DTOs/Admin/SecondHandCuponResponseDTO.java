package com.apiproject.DTOs.Admin;

import com.apiproject.repositories.projection.CuponAdminProjection;

import java.time.LocalDateTime;
import java.util.List;

public record SecondHandCuponResponseDTO(
        Long id,
        String shCuponCode,
        LocalDateTime cuponDateLimit,
        Double discount,
        Integer quantity,
        boolean active,
        Long ownerId,
        List<Long> shProductIds
) {
    public static SecondHandCuponResponseDTO fromProjection(CuponAdminProjection projection, List<Long> shProductIds) {
        return new SecondHandCuponResponseDTO(
                projection.getId(),
                projection.getCuponCode(),
                projection.getCuponDateLimit(),
                projection.getDiscount(),
                projection.getQuantity(),
                CuponResponseDTO.isUsable(projection.getCuponDateLimit(), projection.getQuantity()),
                projection.getOwnerId(),
                shProductIds
        );
    }
}
