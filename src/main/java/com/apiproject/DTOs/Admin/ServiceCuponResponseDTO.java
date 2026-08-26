package com.apiproject.DTOs.Admin;

import com.apiproject.repositories.projection.CuponAdminProjection;

import java.time.LocalDateTime;
import java.util.List;

public record ServiceCuponResponseDTO(
        Long id,
        String serviceCuponCode,
        LocalDateTime cuponDateLimit,
        Double discount,
        Integer quantity,
        boolean active,
        Long ownerId
) {
    public static ServiceCuponResponseDTO fromProjection(CuponAdminProjection projection) {
        return new ServiceCuponResponseDTO(
                projection.getId(),
                projection.getCuponCode(),
                projection.getCuponDateLimit(),
                projection.getDiscount(),
                projection.getQuantity(),
                CuponResponseDTO.isUsable(projection.getCuponDateLimit(), projection.getQuantity()),
                projection.getOwnerId()
        );
    }
}
