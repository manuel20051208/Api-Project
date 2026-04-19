package com.example.apiproject.services.user.client;

import com.example.apiproject.DTOs.Auth.LoginResponseDTO;
import com.example.apiproject.DTOs.Auth.RegisterRequestDTO;
import com.example.apiproject.DTOs.Client.ClientResponseDTO;
import com.example.apiproject.entities.client.UserClient;
import com.example.apiproject.repositories.client.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Service
@RequiredArgsConstructor
public class ClientService {
    private final ClientRepository clientRepository;

    public List<ClientResponseDTO> searchForClientsFullName(String name){
        return clientRepository.findAllByFullNameContainingIgnoreCase(name).stream()
                .map(ClientResponseDTO::fromEntity)
                .toList();
    }

    public List<ClientResponseDTO> searchForClientsEmail(String email){
        return clientRepository.findAllByEmailContainingIgnoreCase(email).stream()
                .map(ClientResponseDTO::fromEntity)
                .toList();
    }

    public List<ClientResponseDTO> searForClientId(Long id){
        return clientRepository.findAllById(id).stream()
                .map(ClientResponseDTO::fromEntity)
                .toList();
    }

    public LoginResponseDTO login(String username, String password) {
        validateCredentials(username, password);

        UserClient userClient = clientRepository.findByUserName(username)
                .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "Usuario o contrasena incorrectos"));

        if (userClient.getPassword() == null || !userClient.getPassword().equals(password)) {
            throw new ResponseStatusException(UNAUTHORIZED, "Usuario o contrasena incorrectos");
        }

        return LoginResponseDTO.fromClient(userClient);
    }

    public LoginResponseDTO register(RegisterRequestDTO registerRequestDTO) {
        validateRegisterRequest(registerRequestDTO);
        validateUniqueFields(registerRequestDTO.username(), registerRequestDTO.email());

        UserClient userClient = new UserClient();
        userClient.setUserName(registerRequestDTO.username());
        userClient.setPassword(registerRequestDTO.password());
        userClient.setFullName(registerRequestDTO.fullName());
        userClient.setEmail(registerRequestDTO.email());
        userClient.setPhone(registerRequestDTO.phone());

        UserClient savedClient = clientRepository.save(userClient);
        return LoginResponseDTO.fromClient(savedClient);
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
        if (clientRepository.existsByUserName(username)) {
            throw new ResponseStatusException(CONFLICT, "El username ya esta registrado");
        }
        if (clientRepository.existsByEmail(email)) {
            throw new ResponseStatusException(CONFLICT, "El email ya esta registrado");
        }
    }
}
