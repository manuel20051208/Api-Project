package com.apiproject.DTOs.Admin;

import com.apiproject.entities.admin.UserAdmin;

public record UserResponseDTO(
        Long id,
        String fullName,
        String email,
        Long phone,
        String photo,
        String businessName) {

    public static UserResponseDTO fromEntity(UserAdmin userAdmin) {
        return new UserResponseDTO(
                userAdmin.getId(),
                userAdmin.getFullName(),
                userAdmin.getEmail(),
                userAdmin.getPhone(),
                userAdmin.getProfilePhotoUrl(),
                userAdmin.getBusinessName());
    }
}
