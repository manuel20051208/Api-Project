package com.example.apiproject.controllers.client;

import com.example.apiproject.DTOs.Auth.LoginRequestDTO;
import com.example.apiproject.DTOs.Auth.LoginResponseDTO;
import com.example.apiproject.DTOs.Auth.RegisterRequestDTO;
import com.example.apiproject.DTOs.Client.ClientResponseDTO;
import com.example.apiproject.DTOs.Client.PaymentCardRequestDTO;
import com.example.apiproject.DTOs.Client.PaymentCardResponseDTO;
import com.example.apiproject.security.AuthenticatedUser;
import com.example.apiproject.services.user.client.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Tag(name = "Client", description = "Endpoints for managing clients")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/client")
public class ClientControllers {
    private final ClientService clientService;

    @Operation(summary = "Basic login for clients")
    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody LoginRequestDTO loginRequestDTO) {
        return clientService.login(
                loginRequestDTO.username(),
                loginRequestDTO.password());
    }

    @Operation(summary = "Basic register for clients")
    @PostMapping("/register")
    public LoginResponseDTO register(@RequestBody RegisterRequestDTO registerRequestDTO) {
        return clientService.register(registerRequestDTO);
    }

    @Operation(summary = "Add a simulated payment card for a client")
    @PostMapping("/{clientId}/payment-cards")
    public PaymentCardResponseDTO addPaymentCard(
            @PathVariable Long clientId,
            @RequestBody PaymentCardRequestDTO paymentCardRequestDTO,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        return clientService.addPaymentCard(clientId, paymentCardRequestDTO, authenticatedUser.id());
    }

    @Operation(summary = "List simulated payment cards for a client")
    @GetMapping("/{clientId}/payment-cards")
    public List<PaymentCardResponseDTO> getPaymentCards(
            @PathVariable Long clientId,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        return clientService.getPaymentCards(clientId, authenticatedUser.id());
    }

    @Operation(summary = "Activate or deactivate a simulated payment card")
    @PatchMapping("/payment-cards/{cardId}/status")
    public PaymentCardResponseDTO updatePaymentCardStatus(
            @PathVariable Long cardId,
            @RequestParam boolean active,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        return clientService.updatePaymentCardStatus(cardId, active, authenticatedUser.id());
    }

    @Operation(summary = "user data (client)")
    @GetMapping("/{clientId}/user-data")
    public ClientResponseDTO getClientData(
            @PathVariable Long clientId,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        if (!clientId.equals(authenticatedUser.id())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes acceder a datos de otro cliente");
        }
        return clientService.searForClientId(clientId);
    }
}
