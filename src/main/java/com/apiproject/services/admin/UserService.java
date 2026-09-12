package com.apiproject.services.admin;

import com.apiproject.DTOs.Admin.UserResponseDTO;
import com.apiproject.DTOs.Auth.LoginAdminResponseDTO;
import com.apiproject.DTOs.Auth.RegisterAdminRequestDTO;
import com.apiproject.DTOs.Client.ClientDescriptionAboutUsersDTO;
import com.apiproject.config.CacheConstants;
import com.apiproject.entities.admin.UserAdmin;
import com.apiproject.exceptions.ResourceNotFoundException;
import com.apiproject.repositories.admin.UserRepository;
import com.apiproject.security.JwtService;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.AllArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
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
    private final CacheManager cacheManager;

    @CachePut(value = CacheConstants.USER_RESPONSE, key = "#userId")
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

    @Transactional
    @CachePut(value = CacheConstants.USER_RESPONSE, key = "#id")
    public UserResponseDTO modifyData(Long id, UserAdmin userAdmin){
        UserAdmin existing = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Optional.ofNullable(userAdmin.getFullName()).ifPresent(existing::setFullName);
        Optional.ofNullable(userAdmin.getEmail()).ifPresent(existing::setEmail);
        Optional.ofNullable(userAdmin.getBusinessName()).ifPresent(existing::setBusinessName);
        Optional.ofNullable(userAdmin.getPhone()).ifPresent(existing::setPhone);
        Optional.ofNullable(userAdmin.getColorTypes()).ifPresent(existing::setColorTypes);

        userRepository.save(existing);

        return UserResponseDTO.fromEntity(existing);
    }

    @Cacheable(value = CacheConstants.USER_RESPONSE, key = "#id", sync = true)
    public UserResponseDTO getUserAdmin (Long id){
        return userRepository.findProfileById(id)
                .map(this::toUserResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }

    @Cacheable(value = "clientDescriptions", key = "#id")
    // obtenemos el usuario para la tienda
    public ClientDescriptionAboutUsersDTO getUserAdminForStore (Long id){

        // Usamos un DTO para no traer datos sensibles al front end
        return userRepository.findProfileById(id)
                .map(this::toStoreResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }

    public LoginAdminResponseDTO login(String email, String password) {
        validateCredentials(email, password);

        UserAdmin userAdmin = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "Correo o contrasena incorrectos"));

        if (!passwordMatches(password, userAdmin)) {
            throw new ResponseStatusException(UNAUTHORIZED, "Correo o contrasena incorrectos");
        }

        String token = jwtService.generateAdminToken(userAdmin.getId(), userAdmin.getEmail(), "ADMIN");
        cacheUserResponse(userAdmin);

        return new LoginAdminResponseDTO(
                userAdmin.getId(),
                userAdmin.getFullName(),
                userAdmin.getEmail(),
                userAdmin.getPhone(),
                userAdmin.getBusinessName(),
                userAdmin.getProfilePhotoUrl(),
                userAdmin.getColorTypes(),
                "ADMIN",
                token,
                "Inicio de sesion exitoso");
    }

    public LoginAdminResponseDTO register(RegisterAdminRequestDTO registerAdminRequestDTO) {
        validateRegisterRequest(registerAdminRequestDTO);
        validateUniqueFields(registerAdminRequestDTO.email());

        UserAdmin userAdmin = new UserAdmin();
        userAdmin.setPassword(passwordEncoder.encode(registerAdminRequestDTO.password()));
        userAdmin.setFullName(registerAdminRequestDTO.fullName());
        userAdmin.setEmail(registerAdminRequestDTO.email());
        userAdmin.setPhone(registerAdminRequestDTO.phone());
        userAdmin.setBusinessName(registerAdminRequestDTO.businessName());

        UserAdmin savedUser = userRepository.save(userAdmin);
        String token = jwtService.generateAdminToken(savedUser.getId(), savedUser.getEmail(), "ADMIN");
        return LoginAdminResponseDTO.fromAdmin(savedUser, token);
    }


    // Validador de contraseñas
    private boolean passwordMatches(String rawPassword, UserAdmin userAdmin) {
        // Sacamos la contraseña del admin suministrado por el 2 parámetro
        String storedPassword = userAdmin.getPassword();

        // Verificamos si la contraseña no se ingreso
        if (storedPassword == null || storedPassword.isBlank()) {
            return false;
        }

        // Verificamos si la contraseña del request es exacta a la del usuario
        if (passwordEncoder.matches(rawPassword, storedPassword)) {
            return true;
        }

        // TODO: eliminar este fallback una vez confirmada la migración completa de usuarios legacy
        // En caso de que el usuario haya creado su contraseña cuando el servidor era inseguro
        // y guardaba la contraseña en texto plano en la BD y no habia sido sifrada, entonces
        // actualizamos la contraseña en la base de datos cifrandola y le damos pase
        if (MessageDigest.isEqual(
                storedPassword.getBytes(StandardCharsets.UTF_8),
                rawPassword.getBytes(StandardCharsets.UTF_8)
        )) {
            userRepository.updatePasswordById(userAdmin.getId(), passwordEncoder.encode(rawPassword));
            return true;
        }

        // Otro caso extraño, invalidar por seguridad
        return false;
    }

    private UserResponseDTO toUserResponse(UserAdmin userAdmin) {
        return new UserResponseDTO(
                userAdmin.getId(),
                userAdmin.getFullName(),
                userAdmin.getEmail(),
                userAdmin.getPhone(),
                userAdmin.getProfilePhotoUrl(),
                userAdmin.getBusinessName(),
                userAdmin.getColorTypes());
    }

    private void cacheUserResponse(UserAdmin userAdmin) {
        var cache = cacheManager.getCache(CacheConstants.USER_RESPONSE);
        if (cache != null) {
            cache.put(userAdmin.getId(), toUserResponse(userAdmin));
        }
    }

    private ClientDescriptionAboutUsersDTO toStoreResponse(UserAdmin userAdmin) {
        return new ClientDescriptionAboutUsersDTO(
                userAdmin.getId(),
                userAdmin.getBusinessName(),
                userAdmin.getProfilePhotoUrl());
    }

    private void validateCredentials(String email, String password) {
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST, "Email y password son obligatorios");
        }
    }

    private void validateRegisterRequest(RegisterAdminRequestDTO registerAdminRequestDTO) {
        if (registerAdminRequestDTO == null) {
            throw new ResponseStatusException(BAD_REQUEST, "El cuerpo de la solicitud es obligatorio");
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

    private void validateUniqueFields(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(CONFLICT, "El email ya esta registrado");
        }
    }
}
