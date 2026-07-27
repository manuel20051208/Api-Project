package com.example.apiproject.DTOs.Admin;

import com.example.apiproject.entities.admin.UserAdmin;
import lombok.NonNull;

public record UserAdminDTO(
        String businessName,
        String fullName,
        Long phone,
        String email) {

    public static UserAdminDTO fromEntity(@NonNull UserAdmin userAdmin) {
        return new UserAdminDTO(userAdmin.getBusinessName(),
                userAdmin.getFullName(),
                userAdmin.getPhone(),
                userAdmin.getEmail());
    }
}
