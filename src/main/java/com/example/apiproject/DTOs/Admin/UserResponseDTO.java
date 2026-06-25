package com.example.apiproject.DTOs.Admin;

import com.example.apiproject.entities.admin.UserAdmin;

public record UserResponseDTO(
        Long id,
        String userName,
        String fullName,
        String email,
        Long phone,
        String photo,
        String businessName) {

    public static UserResponseDTO fromEntity(UserAdmin userAdmin) {
        return new UserResponseDTO(
                userAdmin.getId(),
                userAdmin.getUserName(),
                userAdmin.getFullName(),
                userAdmin.getEmail(),
                userAdmin.getPhone(),
                userAdmin.getProfilePhoto(),
                userAdmin.getBusinessName());
    }
}