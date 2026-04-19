package com.example.apiproject.services.user.admin;

import com.example.apiproject.DTOs.Auth.LoginResponseDTO;
import com.example.apiproject.DTOs.Auth.RegisterRequestDTO;
import com.example.apiproject.entities.admin.UserAdmin;
import com.example.apiproject.repositories.admin.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public LoginResponseDTO login(String username, String password) {
        validateCredentials(username, password);

        UserAdmin userAdmin = userRepository.findByUserName(username)
                .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "Usuario o contrasena incorrectos"));

        if (!userAdmin.getPassword().equals(password)) {
            throw new ResponseStatusException(UNAUTHORIZED, "Usuario o contrasena incorrectos");
        }
        return LoginResponseDTO.fromAdmin(userAdmin);
    }

    public LoginResponseDTO register(RegisterRequestDTO registerRequestDTO) {
        validateRegisterRequest(registerRequestDTO);
        validateUniqueFields(registerRequestDTO.username(), registerRequestDTO.email());

        UserAdmin userAdmin = new UserAdmin();
        userAdmin.setUserName(registerRequestDTO.username());
        userAdmin.setPassword(registerRequestDTO.password());
        userAdmin.setFullName(registerRequestDTO.fullName());
        userAdmin.setEmail(registerRequestDTO.email());
        userAdmin.setPhone(registerRequestDTO.phone());

        UserAdmin savedUser = userRepository.save(userAdmin);
        return LoginResponseDTO.fromAdmin(savedUser);
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
