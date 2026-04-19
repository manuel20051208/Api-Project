package com.example.apiproject.controllers.client;

import com.example.apiproject.DTOs.Auth.LoginRequestDTO;
import com.example.apiproject.DTOs.Auth.LoginResponseDTO;
import com.example.apiproject.DTOs.Auth.RegisterRequestDTO;
import com.example.apiproject.services.user.client.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Client", description = "Endpoints for managing clients")
@RestController
@RequestMapping("/api/client")
public class ClientControllers {
    private final ClientService clientService;

    public ClientControllers(ClientService clientService) {
        this.clientService = clientService;
    }

    @Operation(summary = "Basic login for clients")
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        LoginResponseDTO response = clientService.login(loginRequestDTO.username(), loginRequestDTO.password());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Basic register for clients")
    @PostMapping("/register")
    public ResponseEntity<LoginResponseDTO> register(@RequestBody RegisterRequestDTO registerRequestDTO) {
        LoginResponseDTO response = clientService.register(registerRequestDTO);
        return ResponseEntity.ok(response);
    }
}
