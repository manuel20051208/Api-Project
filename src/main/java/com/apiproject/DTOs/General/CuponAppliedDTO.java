package com.apiproject.DTOs.General;

import java.math.BigDecimal;

import com.apiproject.entities.admin.Cupon;

import java.time.LocalDateTime;

public record CuponAppliedDTO(
        Long id,
        String code,
        BigDecimal discount,
        LocalDateTime cuponDateLimit
) {
    public static CuponAppliedDTO fromEntity(Cupon cupon) {
        return new CuponAppliedDTO(
                cupon.getId(),
                cupon.getCuponCode(),
                cupon.getDiscount(),
                cupon.getCuponDateLimit()
        );
    }
}
