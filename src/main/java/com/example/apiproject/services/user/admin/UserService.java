package com.example.apiproject.services.user.admin;

import com.example.apiproject.DTOs.Admin.UserResponseDTO;
import com.example.apiproject.DTOs.Auth.LoginResponseDTO;
import com.example.apiproject.DTOs.Auth.RegisterRequestDTO;
import com.example.apiproject.entities.admin.UserAdmin;
import com.example.apiproject.exceptions.ResourceNotFoundException;
import com.example.apiproject.repositories.admin.UserRepository;
import com.example.apiproject.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
            try {
                Path rootFolder = Paths.get("uploads/perfiles");
                Files.createDirectories(rootFolder);
            } catch (IOException e) {
                throw new RuntimeException("No se pudo crear la carpeta de perfiles");
        }
    }

    public void subirFotoPerfil (Long id, MultipartFile file){
        UserAdmin userAdmin = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }

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

    public UserResponseDTO getUserAdmin (Long id){
        return userRepository.findProfileById(id)
                .map(this::toUserResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }

    public LoginResponseDTO login(String username, String password) {
        validateCredentials(username, password);

        UserAdmin userAdmin = userRepository.findUserAdminByUserName(username)
                .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "Usuario o contrasena incorrectos"));

        if (!passwordMatches(password, userAdmin)) {
            throw new ResponseStatusException(UNAUTHORIZED, "Usuario o contrasena incorrectos");
        }

        String token = jwtService.generateToken(userAdmin.getId(), userAdmin.getUserName(), "ADMIN");
        return new LoginResponseDTO(
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

    public LoginResponseDTO register(RegisterRequestDTO registerRequestDTO) {
        validateRegisterRequest(registerRequestDTO);
        validateUniqueFields(registerRequestDTO.username(), registerRequestDTO.email());

        UserAdmin userAdmin = new UserAdmin();
        userAdmin.setUserName(registerRequestDTO.username());
        userAdmin.setPassword(passwordEncoder.encode(registerRequestDTO.password()));
        userAdmin.setFullName(registerRequestDTO.fullName());
        userAdmin.setEmail(registerRequestDTO.email());
        userAdmin.setPhone(registerRequestDTO.phone());
        userAdmin.setBusinessName(registerRequestDTO.businessName());

        UserAdmin savedUser = userRepository.save(userAdmin);
        String token = jwtService.generateToken(savedUser.getId(), savedUser.getUserName(), "ADMIN");
        return LoginResponseDTO.fromAdmin(savedUser, token);
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
                userAdmin.getBusinessName());
    }

    private void validateCredentials(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST, "Username y password son obligatorios");
        }
    }

    private void validateRegisterRequest(RegisterRequestDTO registerRequestDTO) {
        if (registerRequestDTO == null) {
            throw new ResponseStatusException(BAD_REQUEST, "El cuerpo de la solicitud es obligatorio");
        }
        if (registerRequestDTO.username() == null || registerRequestDTO.username().isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST, "El username es obligatorio");
        }
        if (registerRequestDTO.password() == null || registerRequestDTO.password().isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST, "El password es obligatorio");
        }
        if (registerRequestDTO.fullName() == null || registerRequestDTO.fullName().isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST, "El fullName es obligatorio");
        }
        if (registerRequestDTO.email() == null || registerRequestDTO.email().isBlank()) {
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
