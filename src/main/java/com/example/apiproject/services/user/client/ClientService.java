package com.example.apiproject.services.user.client;

import com.example.apiproject.DTOs.Auth.LoginResponseDTO;
import com.example.apiproject.DTOs.Auth.RegisterRequestDTO;
import com.example.apiproject.DTOs.Client.ClientResponseDTO;
import com.example.apiproject.DTOs.Client.PaymentCardRequestDTO;
import com.example.apiproject.DTOs.Client.PaymentCardResponseDTO;
import com.example.apiproject.entities.client.PaymentCard;
import com.example.apiproject.entities.client.UserClient;
import com.example.apiproject.exceptions.ResourceNotFoundException;
import com.example.apiproject.repositories.client.ClientRepository;
import com.example.apiproject.repositories.client.PaymentCardRepository;
import com.example.apiproject.repositories.projection.ClientHistoryProjection;
import com.example.apiproject.repositories.projection.PaymentCardOwnerProjection;
import com.example.apiproject.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
public class ClientService {
    private final ClientRepository clientRepository;
    private final PaymentCardRepository paymentCardRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public List<ClientResponseDTO> searchForClientsFullName(String name) {
        return clientRepository.findSummaryByFullNameContainingIgnoreCase(name).stream()
                .map(this::toClientResponse)
                .toList();
    }

    public List<ClientResponseDTO> searchForClientsEmail(String email) {
        return clientRepository.findSummaryByEmailContainingIgnoreCase(email).stream()
                .map(this::toClientResponse)
                .toList();
    }

    @Cacheable(value = "ClientHistory", key = "#userId")
    public List<ClientHistoryProjection> showBuys(Long userId) {
        return clientRepository.showClientBuy(userId);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "clientResponse", key = "#id")
    public ClientResponseDTO searForClientId(Long id) {
        return clientRepository.findSummaryById(id)
                .map(this::toClientResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }

    @Transactional
    public LoginResponseDTO login(String username, String password) {
        validateCredentials(username, password);

        UserClient userClient = clientRepository.findLoginByUserName(username)
                .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "Usuario o contrasena incorrectos"));

        if (!passwordMatches(password, userClient)) {
            throw new ResponseStatusException(UNAUTHORIZED, "Usuario o contrasena incorrectos");
        }

        String token = jwtService.generateToken(userClient.getId(), userClient.getUserName(), "CLIENT");
        return new LoginResponseDTO(
                userClient.getId(),
                userClient.getUserName(),
                userClient.getFullName(),
                userClient.getEmail(),
                userClient.getPhone(),
                null,
                "CLIENT",
                token,
                "Inicio de sesion exitoso");
    }

    public LoginResponseDTO register(RegisterRequestDTO registerRequestDTO) {
        validateRegisterRequest(registerRequestDTO);
        validateUniqueFields(registerRequestDTO.username(), registerRequestDTO.email());

        UserClient userClient = new UserClient();
        userClient.setUserName(registerRequestDTO.username());
        userClient.setPassword(passwordEncoder.encode(registerRequestDTO.password()));
        userClient.setFullName(registerRequestDTO.fullName());
        userClient.setEmail(registerRequestDTO.email());
        userClient.setPhone(registerRequestDTO.phone());

        UserClient savedClient = clientRepository.save(userClient);
        String token = jwtService.generateToken(savedClient.getId(), savedClient.getUserName(), "CLIENT");
        return LoginResponseDTO.fromClient(savedClient, token);
    }

    @Transactional
    @CacheEvict(value = "Payment", key = "#authenticatedClientId")
    public PaymentCardResponseDTO addPaymentCard(PaymentCardRequestDTO requestDTO, Long authenticatedClientId) {
        validateAuthenticatedClient(authenticatedClientId);

        if (requestDTO == null) {
            throw new ResponseStatusException(BAD_REQUEST, "El cuerpo de la solicitud es obligatorio");
        }
        if (requestDTO.lastFour() == null || !requestDTO.lastFour().matches("\\d{4}")) {
            throw new ResponseStatusException(BAD_REQUEST, "lastFour debe tener exactamente 4 digitos");
        }

        if (!clientRepository.existsById(authenticatedClientId)) {
            throw new ResponseStatusException(BAD_REQUEST, "Cliente no encontrado: " + authenticatedClientId);
        }
        UserClient userClient = clientRepository.getReferenceById(authenticatedClientId);

        PaymentCard paymentCard = new PaymentCard();
        paymentCard.setUserClient(userClient);
        paymentCard.setCardHolderName(requestDTO.cardHolderName());
        paymentCard.setBrand(requestDTO.brand());
        paymentCard.setLastFour(requestDTO.lastFour());
        paymentCard.setActive(requestDTO.active() == null || requestDTO.active());

        return PaymentCardResponseDTO.fromEntity(paymentCardRepository.save(paymentCard));
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "Payment", key = "#authenticatedClientId")
    public List<PaymentCardResponseDTO> getPaymentCards(Long authenticatedClientId) {
        validateAuthenticatedClient(authenticatedClientId);

        return paymentCardRepository.findDetailsByUserClientIdOrderByCreatedAtDesc(authenticatedClientId).stream()
                .map(PaymentCardResponseDTO::fromProjection)
                .toList();
    }

    @Transactional
    @CacheEvict(value = "Payment", key = "#authenticatedClientId")
    public PaymentCardResponseDTO updatePaymentCardStatus(Long cardId, boolean active, Long authenticatedClientId) {
        Long cardClientId = paymentCardRepository.findOwnerById(cardId)
                .map(PaymentCardOwnerProjection::getClientId)
                .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "Tarjeta no encontrada: " + cardId));

        validateAuthenticatedClient(authenticatedClientId);

        if (!cardClientId.equals(authenticatedClientId)) {
            throw new ResponseStatusException(FORBIDDEN, "No tienes permiso sobre esta tarjeta");
        }

        paymentCardRepository.updateActiveById(cardId, active);

        return paymentCardRepository.findDetailsById(cardId)
                .map(PaymentCardResponseDTO::fromProjection)
                .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "Tarjeta no encontrada: " + cardId));
    }

    private boolean passwordMatches(String rawPassword, UserClient userClient) {
        String storedPassword = userClient.getPassword();
        if (storedPassword == null || storedPassword.isBlank()) {
            return false;
        }

        if (passwordEncoder.matches(rawPassword, storedPassword)) {
            return true;
        }

        if (storedPassword.equals(rawPassword)) {
            clientRepository.updatePasswordById(userClient.getId(), passwordEncoder.encode(rawPassword));
            return true;
        }

        return false;
    }

    private ClientResponseDTO toClientResponse(UserClient userClient) {
        return new ClientResponseDTO(
                userClient.getId(),
                userClient.getFullName(),
                userClient.getEmail(),
                userClient.getUserName(),
                userClient.getPhone());
    }

    private void validateAuthenticatedClient(Long authenticatedClientId) {
        if (authenticatedClientId == null) {
            throw new ResponseStatusException(FORBIDDEN, "No puedes acceder a datos de otro cliente");
        }
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
