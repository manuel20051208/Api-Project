package com.example.apiproject.services.admin;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.apiproject.DTOs.Admin.UserResponseDTO;
import com.example.apiproject.DTOs.Auth.LoginAdminResponseDTO;
import com.example.apiproject.DTOs.Auth.RegisterAdminRequestDTO;
import com.example.apiproject.DTOs.Client.ClientDescriptionAboutUsersDTO;
import com.example.apiproject.entities.admin.UserAdmin;
import com.example.apiproject.exceptions.ResourceNotFoundException;
import com.example.apiproject.repositories.admin.UserRepository;
import com.example.apiproject.security.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import static org.springframework.http.HttpStatus.*;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final Cloudinary cloudinary;

    @CacheEvict(value = "users", key = "#userId")
    public UserResponseDTO subirFotoPerfil(Long userId, MultipartFile file) throws IOException {
        UserAdmin userAdmin = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (file.isEmpty()) {
            throw new IOException("No se puede cargar el archivo.");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IOException("Solo se permiten archivos de imagen.");
        }

        if (userAdmin.getProfilePhoto() != null) {
            cloudinary.uploader().destroy(userAdmin.getProfilePhoto(), ObjectUtils.emptyMap());
        }

        Map<String, Object> uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap("folder", "perfiles")
        );

        userAdmin.setProfilePhoto((String) uploadResult.get("public_id"));
        userAdmin.setProfilePhotoUrl((String) uploadResult.get("secure_url"));
        userRepository.save(userAdmin);

        return toUserResponse(userAdmin);
    }

    @CacheEvict(value = "users", key = "#id")
    public UserResponseDTO modifyData(Long id, UserAdmin userAdmin){
        UserAdmin existing = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Optional.ofNullable(userAdmin.getFullName()).ifPresent(existing::setFullName);
        Optional.ofNullable(userAdmin.getEmail()).ifPresent(existing::setEmail);
        Optional.ofNullable(userAdmin.getPassword())
                .filter(password -> !password.isBlank())
                .map(passwordEncoder::encode)
                .ifPresent(existing::setPassword);
        Optional.ofNullable(userAdmin.getBusinessName()).ifPresent(existing::setBusinessName);
        Optional.ofNullable(userAdmin.getPhone()).ifPresent(existing::setPhone);

        userRepository.save(existing);

        return UserResponseDTO.fromEntity(existing);
    }

    @Cacheable(value = "users", key = "#id")
    public UserResponseDTO getUserAdmin (Long id){
        return userRepository.findProfileById(id)
                .map(this::toUserResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }

    @Cacheable(value = "clientDescriptions", key = "#id")
    public ClientDescriptionAboutUsersDTO getUserAdminForStore (Long id){
        return userRepository.findProfileById(id)
                .map(this::toStoreResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }

    public LoginAdminResponseDTO login(String username, String password) {
        validateCredentials(username, password);

        UserAdmin userAdmin = userRepository.findUserAdminByUserName(username)
                .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "Usuario o contrasena incorrectos"));

        if (!passwordMatches(password, userAdmin)) {
            throw new ResponseStatusException(UNAUTHORIZED, "Usuario o contrasena incorrectos");
        }

        String token = jwtService.generateAdminToken(userAdmin.getId(), userAdmin.getUserName(), "ADMIN");
        return new LoginAdminResponseDTO(
                userAdmin.getId(),
                userAdmin.getUserName(),
                userAdmin.getFullName(),
                userAdmin.getEmail(),
                userAdmin.getPhone(),
                userAdmin.getBusinessName(),
                "ADMIN",
                token,
                "Inicio de sesion exitoso");
    }

    public LoginAdminResponseDTO register(RegisterAdminRequestDTO registerAdminRequestDTO) {
        validateRegisterRequest(registerAdminRequestDTO);
        validateUniqueFields(registerAdminRequestDTO.username(), registerAdminRequestDTO.email());

        UserAdmin userAdmin = new UserAdmin();
        userAdmin.setUserName(registerAdminRequestDTO.username());
        userAdmin.setPassword(passwordEncoder.encode(registerAdminRequestDTO.password()));
        userAdmin.setFullName(registerAdminRequestDTO.fullName());
        userAdmin.setEmail(registerAdminRequestDTO.email());
        userAdmin.setPhone(registerAdminRequestDTO.phone());
        userAdmin.setBusinessName(registerAdminRequestDTO.businessName());

        UserAdmin savedUser = userRepository.save(userAdmin);
        String token = jwtService.generateAdminToken(savedUser.getId(), savedUser.getUserName(), "ADMIN");
        return LoginAdminResponseDTO.fromAdmin(savedUser, token);
    }

    private boolean passwordMatches(String rawPassword, UserAdmin userAdmin) {
        String storedPassword = userAdmin.getPassword();
        if (storedPassword == null || storedPassword.isBlank()) {
            return false;
        }

        if (passwordEncoder.matches(rawPassword, storedPassword)) {
            return true;
        }

        if (storedPassword.equals(rawPassword)) {
            userRepository.updatePasswordById(userAdmin.getId(), passwordEncoder.encode(rawPassword));
            return true;
        }

        return false;
    }

    private UserResponseDTO toUserResponse(UserAdmin userAdmin) {
        return new UserResponseDTO(
                userAdmin.getId(),
                userAdmin.getUserName(),
                userAdmin.getFullName(),
                userAdmin.getEmail(),
                userAdmin.getPhone(),
                userAdmin.getProfilePhotoUrl(),
                userAdmin.getBusinessName());
    }

    private ClientDescriptionAboutUsersDTO toStoreResponse(UserAdmin userAdmin) {
        return new ClientDescriptionAboutUsersDTO(
                userAdmin.getId(),
                userAdmin.getBusinessName(),
                userAdmin.getProfilePhotoUrl());
    }

    private void validateCredentials(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST, "Username y password son obligatorios");
        }
    }

    private void validateRegisterRequest(RegisterAdminRequestDTO registerAdminRequestDTO) {
        if (registerAdminRequestDTO == null) {
            throw new ResponseStatusException(BAD_REQUEST, "El cuerpo de la solicitud es obligatorio");
        }
        if (registerAdminRequestDTO.username() == null || registerAdminRequestDTO.username().isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST, "El username es obligatorio");
        }
        if (registerAdminRequestDTO.password() == null || registerAdminRequestDTO.password().isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST, "El password es obligatorio");
        }
        if (registerAdminRequestDTO.fullName() == null || registerAdminRequestDTO.fullName().isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST, "El fullName es obligatorio");
        }
        if (registerAdminRequestDTO.email() == null || registerAdminRequestDTO.email().isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST, "El email es obligatorio");
        }
    }

    private void validateUniqueFields(String username, String email) {
        if (userRepository.existsByUserName(username)) {
            throw new ResponseStatusException(CONFLICT, "El username ya esta registrado");
        }
        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(CONFLICT, "El email ya esta registrado");
        }
    }
}