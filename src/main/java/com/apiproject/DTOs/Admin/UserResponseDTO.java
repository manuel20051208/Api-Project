package com.apiproject.DTOs.Admin;

import com.apiproject.entities.admin.UserAdmin;
import com.apiproject.enums.ColorTypes;

public record UserResponseDTO(
        Long id,
        String fullName,
        String email,
        Long phone,
        String photo,
        String businessName,
        ColorTypes colorTypes) {

    public static UserResponseDTO fromEntity(UserAdmin userAdmin) {
        return new UserResponseDTO(
                userAdmin.getId(),
                userAdmin.getFullName(),
                userAdmin.getEmail(),
                userAdmin.getPhone(),
                userAdmin.getProfilePhotoUrl(),
                userAdmin.getBusinessName(),
                userAdmin.getColorTypes());
    }
}
