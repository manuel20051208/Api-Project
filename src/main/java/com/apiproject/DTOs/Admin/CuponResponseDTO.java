package com.apiproject.DTOs.Admin;

import com.apiproject.entities.admin.Cupon;
import com.apiproject.repositories.projection.CuponAdminProjection;

import java.time.LocalDateTime;
import java.util.List;

public record CuponResponseDTO(
        Long id,
        String cuponCode,
        LocalDateTime cuponDateLimit,
        Double discount,
        Integer quantity,
        boolean active,
        Long ownerId,
        List<Long> productIds
) {
    public static CuponResponseDTO fromProjection(CuponAdminProjection projection, List<Long> productIds) {
        return new CuponResponseDTO(
                projection.getId(),
                projection.getCuponCode(),
                projection.getCuponDateLimit(),
                projection.getDiscount(),
                projection.getQuantity(),
                isUsable(projection.getCuponDateLimit(), projection.getQuantity()),
                projection.getOwnerId(),
                productIds
        );
    }

    public static CuponResponseDTO fromEntity(Cupon cupon, Long ownerId, List<Long> productIds) {
        return new CuponResponseDTO(
                cupon.getId(),
                cupon.getCuponCode(),
                cupon.getCuponDateLimit(),
                cupon.getDiscount(),
                cupon.getQuantity(),
                isUsable(cupon.getCuponDateLimit(), cupon.getQuantity()),
                ownerId,
                productIds
        );
    }

    public static boolean isUsable(LocalDateTime cuponDateLimit, Integer quantity) {
        return cuponDateLimit != null
                && cuponDateLimit.isAfter(LocalDateTime.now())
                && (quantity == null || quantity > 0);
    }
}
