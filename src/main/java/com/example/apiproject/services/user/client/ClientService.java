package com.example.apiproject.services.user.client;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.apiproject.DTOs.Auth.LoginClientResponseDTO;
import com.example.apiproject.DTOs.Auth.RegisterClientRequestDTO;
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
import org.springframework.cache.annotation.Caching;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
public class ClientService {
    private final ClientRepository clientRepository;
    private final PaymentCardRepository paymentCardRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final Cloudinary cloudinary;

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

    @Transactional(readOnly = true)
    public ResponseEntity<Void> obtenerFotoPerfil(Long clientId) {
        validateAuthenticatedClient(clientId);

        UserClient userClient = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (isRemoteUrl(userClient.getPhoto())) {
            return ResponseEntity.status(FOUND)
                    .location(URI.create(userClient.getPhoto()))
                    .build();
        }

        if (!StringUtils.hasText(userClient.getPhoto())) {
            throw new ResponseStatusException(NOT_FOUND, "El cliente no tiene foto de perfil");
        }

        return ResponseEntity.status(FOUND)
                .location(URI.create(cloudinary.url().secure(true).generate(userClient.getPhoto())))
                .build();
    }

    @Transactional
    @CacheEvict(value = "clientResponse", key = "#clientId")
    public ClientResponseDTO subirFotoPerfil(Long clientId, MultipartFile file) throws IOException {
        validateAuthenticatedClient(clientId);
        validateProfilePhoto(file);

        UserClient userClient = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Map<String, Object> uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap("folder", "clientes/perfiles")
        );

        String oldPhoto = userClient.getPhoto();
        userClient.setPhoto((String) uploadResult.get("secure_url"));
        clientRepository.save(userClient);
        deletePreviousPhotoIfPossible(oldPhoto);

        return toClientResponse(userClient);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "clientResponse", key = "#id"),
            @CacheEvict(value = "ClientHistory", key = "#id")
    })
    public ClientResponseDTO modifyData(Long id, UserClient userClient) {
        validateAuthenticatedClient(id);
        validateModifyRequest(userClient);

        UserClient existing = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Optional.ofNullable(userClient.getFullName()).ifPresent(existing::setFullName);
        Optional.ofNullable(userClient.getEmail())
                .ifPresent(email -> {
                    validateEmailForUpdate(email);
                    validateUniqueEmailForUpdate(email, id);
                    existing.setEmail(email);
                });
        Optional.ofNullable(userClient.getPassword())
                .filter(password -> !password.isBlank())
                .map(passwordEncoder::encode)
                .ifPresent(existing::setPassword);
        Optional.ofNullable(userClient.getPhone()).ifPresent(existing::setPhone);
        Optional.ofNullable(userClient.getAddress()).ifPresent(existing::setAddress);

        clientRepository.save(existing);

        return toClientResponse(existing);
    }

    @Transactional
    public LoginClientResponseDTO loginClient(String email, String password) {
        validateCredentials(email, password);

        UserClient userClient = clientRepository.findLoginByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "Usuario o contrasena incorrectos"));

        if (!passwordMatches(password, userClient)) {
            throw new ResponseStatusException(UNAUTHORIZED, "Usuario o contrasena incorrectos");
        }

        String token = jwtService.generateClientToken(userClient.getId(), userClient.getEmail(), "CLIENT");
        return new LoginClientResponseDTO(
                userClient.getId(),
                userClient.getFullName(),
                userClient.getEmail(),
                userClient.getPhone(),
                userClient.getAddress(),
                userClient.getCreatedAt(),
                "CLIENT",
                token,
                "Inicio de sesion exitoso");
    }

    public LoginClientResponseDTO register(RegisterClientRequestDTO registerClientRequestDTO) {
        validateRegisterRequest(registerClientRequestDTO);
        validateUniqueEmail(registerClientRequestDTO.email());

        UserClient userClient = new UserClient();
        userClient.setPassword(passwordEncoder.encode(registerClientRequestDTO.password()));
        userClient.setFullName(registerClientRequestDTO.fullName());
        userClient.setEmail(registerClientRequestDTO.email());
        userClient.setPhone(registerClientRequestDTO.phone());
        userClient.setAddress(registerClientRequestDTO.address());

        UserClient savedClient = clientRepository.save(userClient);
        String token = jwtService.generateClientToken(savedClient.getId(), savedClient.getEmail(), "CLIENT");
        return LoginClientResponseDTO.fromClient(savedClient, token);
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
                userClient.getPhone(),
                userClient.getPaymentCards(),
                userClient.getAddress(),
                userClient.getCreatedAt(),
                userClient.getPhoto());
    }

    // la parte de fotos fue implementada con ayuda de la IA (linea 262 - 313)
    private void deletePreviousPhotoIfPossible(String oldPhoto) {
        if (!StringUtils.hasText(oldPhoto)) {
            return;
        }

        try {
            String publicId = resolveCloudinaryPublicId(oldPhoto);
            if (StringUtils.hasText(publicId)) {
                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            }
        } catch (IOException ignored) {
            // Old photo cleanup should not block a successful new upload.
        }
    }

    private void validateProfilePhoto(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "No se puede cargar un archivo vacio");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new ResponseStatusException(BAD_REQUEST, "Solo se permiten archivos de imagen");
        }
    }

    private boolean isRemoteUrl(String photo) {
        return StringUtils.hasText(photo) && (photo.startsWith("http://") || photo.startsWith("https://"));
    }

    private String resolveCloudinaryPublicId(String photo) {
        if (!isRemoteUrl(photo)) {
            return photo;
        }

        URI photoUri = URI.create(photo);
        String path = photoUri.getPath();
        String uploadMarker = "/upload/";
        int uploadIndex = path.indexOf(uploadMarker);
        if (uploadIndex < 0) {
            return null;
        }

        String publicPath = path.substring(uploadIndex + uploadMarker.length());
        String[] pathParts = publicPath.split("/", 2);
        if (pathParts.length == 2 && pathParts[0].matches("v\\d+")) {
            publicPath = pathParts[1];
        }

        return StringUtils.stripFilenameExtension(publicPath);
    }

    private void validateAuthenticatedClient(Long authenticatedClientId) {
        if (authenticatedClientId == null) {
            throw new ResponseStatusException(FORBIDDEN, "No puedes acceder a datos de otro cliente");
        }
    }

    private void validateCredentials(String email, String password) {
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST, "Email y password son obligatorios");
        }
    }

    private void validateRegisterRequest(RegisterClientRequestDTO registerClientRequestDTO) {
        if (registerClientRequestDTO == null) {
            throw new ResponseStatusException(BAD_REQUEST, "El cuerpo de la solicitud es obligatorio");
        }
        if (registerClientRequestDTO.password() == null || registerClientRequestDTO.password().isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST, "El password es obligatorio");
        }
        if (registerClientRequestDTO.fullName() == null || registerClientRequestDTO.fullName().isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST, "El fullName es obligatorio");
        }
        if (registerClientRequestDTO.email() == null || registerClientRequestDTO.email().isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST, "El email es obligatorio");
        }
    }

    private void validateModifyRequest(UserClient userClient) {
        if (userClient == null) {
            throw new ResponseStatusException(BAD_REQUEST, "El cuerpo de la solicitud es obligatorio");
        }
    }

    private void validateEmailForUpdate(String email) {
        if (!StringUtils.hasText(email)) {
            throw new ResponseStatusException(BAD_REQUEST, "El email no puede estar vacio");
        }
    }

    private void validateUniqueEmail(String email) {
        if (clientRepository.existsByEmail(email)) {
            throw new ResponseStatusException(CONFLICT, "El email ya esta registrado");
        }
    }

    private void validateUniqueEmailForUpdate(String email, Long id) {
        clientRepository.findLoginByEmail(email)
                .filter(client -> !client.getId().equals(id))
                .ifPresent(client -> {
                    throw new ResponseStatusException(CONFLICT, "El email ya esta registrado");
                });
    }
}
