package com.apiproject.controllers.client;

import com.apiproject.DTOs.Auth.LoginClientRequestDTO;
import com.apiproject.DTOs.Auth.LoginClientResponseDTO;
import com.apiproject.DTOs.Auth.RegisterClientRequestDTO;
import com.apiproject.DTOs.Client.ClientDescriptionAboutUsersDTO;
import com.apiproject.DTOs.Client.ClientResponseDTO;
import com.apiproject.DTOs.Client.PaymentCardRequestDTO;
import com.apiproject.DTOs.Client.PaymentCardResponseDTO;
import com.apiproject.entities.client.UserClient;
import com.apiproject.repositories.projection.ClientHistoryProjection;
import com.apiproject.security.AuthenticatedUser;
import com.apiproject.services.admin.UserService;
import com.apiproject.services.client.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

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
    public Page<ClientHistoryProjection> getClientHistory(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "occurredAt,desc") String sort) {
        if (size < 1 || size > 100) {
            throw new ResponseStatusException(BAD_REQUEST, "size debe estar entre 1 y 100");
        }
        if (page < 0) {
            throw new ResponseStatusException(BAD_REQUEST, "page no puede ser negativo");
        }
        Sort orders = parseSort(sort);
        return clientService.showBuysPaginated(authenticatedUser.id(), PageRequest.of(page, size, orders));
    }

    /** Convierte "occurredAt,desc" (convención Spring Data) al Column Sort sobre la vista. */
    private Sort parseSort(String sort) {
        String[] parts = sort.split(",");
        String field = parts[0].trim();
        boolean desc = parts.length > 1 && parts[1].trim().equalsIgnoreCase("desc");

        if (field.equalsIgnoreCase("occurredAt") || field.equalsIgnoreCase("occurred_at")) {
            return desc ? Sort.by(Sort.Direction.DESC, "occurred_at")
                        : Sort.by(Sort.Direction.ASC, "occurred_at");
        }
        return Sort.by(Sort.Direction.DESC, "occurred_at"); // default DESC por si viene algo raro
    }
}
