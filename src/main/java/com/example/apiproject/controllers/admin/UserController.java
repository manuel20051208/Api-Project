package com.example.apiproject.controllers.admin;

import com.example.apiproject.DTOs.Admin.UserResponseDTO;
import com.example.apiproject.DTOs.Auth.LoginRequestDTO;
import com.example.apiproject.DTOs.Auth.LoginResponseDTO;
import com.example.apiproject.DTOs.Auth.RegisterRequestDTO;
import com.example.apiproject.entities.admin.UserAdmin;
import com.example.apiproject.security.AuthenticatedUser;
import com.example.apiproject.services.user.admin.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@Tag(name = "User", description = "Endpoints for managing users")
@DynamicUpdate
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    @Operation(summary = "Basic login for admin users")
    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody LoginRequestDTO loginRequestDTO) {
        return userService.login(loginRequestDTO.username(), loginRequestDTO.password());
    }

    @Operation(summary = "Basic register for admin users")
    @PostMapping("/register")
    public LoginResponseDTO register(@RequestBody RegisterRequestDTO registerRequestDTO) {
        return userService.register(registerRequestDTO);
    }

    @Operation(summary = "user data (admin)")
    @GetMapping("/{adminId}/admin")
    public UserResponseDTO gerAdminData(
            @PathVariable Long adminId,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        validateSelf(adminId, authenticatedUser);
        return userService.getUserAdmin(adminId);
    }

    @Operation(summary = "modify data")
    @PatchMapping(value = "/{adminId}/modify")
    public UserResponseDTO modify(@PathVariable Long adminId,
                                  @RequestBody @Valid UserAdmin userAdmin,
                                  @AuthenticationPrincipal AuthenticatedUser authenticatedUser){
        validateSelf(adminId, authenticatedUser);
        return userService.modifyData(adminId, userAdmin);
    }

    @Operation(summary = "Upload a profile photo")
    @PatchMapping(value = "/{userId}/upload-profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Resource> uploadPhoto(
            @PathVariable Long userId,
            @RequestPart("profilePhoto") MultipartFile profilePhoto,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser) throws IOException {
        validateSelf(userId, authenticatedUser);
        return userService.subirFotoPerfil(userId, profilePhoto);
    }

    @Operation(summary = "Get profile photo")
    @GetMapping("/{userId}/profile-photo")
    public ResponseEntity<Resource> getProfilePhoto(@PathVariable Long userId) throws IOException {
        return userService.obtenerFotoPerfil(userId);
    }

    private void validateSelf(Long adminId, AuthenticatedUser authenticatedUser) {
        if (!adminId.equals(authenticatedUser.id())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes acceder a datos de otro usuario admin");
        }
    }
}
