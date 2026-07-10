package com.example.apiproject.controllers.client;

import com.example.apiproject.DTOs.Auth.*;
import com.example.apiproject.DTOs.Client.ClientDescriptionAboutUsersDTO;
import com.example.apiproject.DTOs.Client.ClientResponseDTO;
import com.example.apiproject.DTOs.Client.PaymentCardRequestDTO;
import com.example.apiproject.DTOs.Client.PaymentCardResponseDTO;
import com.example.apiproject.entities.client.UserClient;
import com.example.apiproject.repositories.projection.ClientHistoryProjection;
import com.example.apiproject.security.AuthenticatedUser;
import com.example.apiproject.services.user.admin.UserService;
import com.example.apiproject.services.user.client.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Tag(name = "Client", description = "Endpoints for managing clients")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/client")
public class ClientControllers {
    private final ClientService clientService;
    private final UserService userService;

    @Operation(summary = "Basic login for clients")
    @PostMapping("/login")
    public LoginClientResponseDTO login(
            @RequestBody LoginClientRequestDTO loginRequestDTO
    ) {
        return clientService.loginClient(
                loginRequestDTO.email(),
                loginRequestDTO.password());
    }

    @Operation(summary = "user data (admin)")
    @GetMapping("/{adminId}/admin")
    public ClientDescriptionAboutUsersDTO gerAdminDataForStore(
            @PathVariable Long adminId
    ) {
        return userService.getUserAdminForStore(adminId);
    }

    @Operation(summary = "Basic register for clients")
    @PostMapping("/register")
    public LoginClientResponseDTO register(
            @RequestBody RegisterClientRequestDTO registerClientRequestDTO
    ) {
        return clientService.register(registerClientRequestDTO);
    }

    @Operation(summary = "modify data")
    @PatchMapping(value = "/modify")
    public ClientResponseDTO modify(
            @RequestBody UserClient userClient,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        return clientService.modifyData(authenticatedUser.id(), userClient);
    }

    @PatchMapping(value = "/upload-profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ClientResponseDTO uploadProfilePhoto(
            @RequestPart("profilePhoto") MultipartFile profilePhoto,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) throws IOException {
        return clientService.subirFotoPerfil(authenticatedUser.id(), profilePhoto);
    }

    @GetMapping("/profile-photo")
    public ResponseEntity<Void> getProfilePhoto(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        return clientService.obtenerFotoPerfil(authenticatedUser.id());
    }

    @Operation(summary = "Add a simulated payment card for a client")
    @PostMapping("/payment-cards")
    public PaymentCardResponseDTO addPaymentCard(
            @RequestBody PaymentCardRequestDTO paymentCardRequestDTO,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        return clientService.addPaymentCard(paymentCardRequestDTO, authenticatedUser.id());
    }

    @Operation(summary = "List simulated payment cards for a client")
    @GetMapping("/payment-cards")
    public List<PaymentCardResponseDTO> getPaymentCards(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        return clientService.getPaymentCards(authenticatedUser.id());
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
    @GetMapping("/user-data")
    public ClientResponseDTO getClientData(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        return clientService.searForClientId(authenticatedUser.id());
    }

    @Operation(summary = "show client's history of buy")
    @GetMapping("/user-payments")
    public List<ClientHistoryProjection> getClientHistory(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        return clientService.showBuys(authenticatedUser.id());
    }
}
