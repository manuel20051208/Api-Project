package com.example.apiproject.DTOs.Admin;

import com.example.apiproject.entities.admin.UserAdmin;
import org.jspecify.annotations.NonNull;

public record UserResponseDTO(
        Long id,
        String userName,
        String fullName,
        String email,
        Long phone) {

    public static UserResponseDTO fromEntity(@NonNull UserAdmin userAdmin) {
        return new UserResponseDTO(
                userAdmin.getId(),
                userAdmin.getUserName(),
                userAdmin.getFullName(),
                userAdmin.getEmail(),
                userAdmin.getPhone());
    }
}
