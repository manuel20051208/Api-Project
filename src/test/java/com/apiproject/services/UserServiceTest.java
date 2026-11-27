package com.apiproject.services;

import com.apiproject.DTOs.Admin.UpdateAdminRequestDTO;
import com.apiproject.DTOs.Admin.UserResponseDTO;
import com.apiproject.entities.admin.UserAdmin;
import com.apiproject.enums.ColorTypes;
import com.apiproject.exceptions.ResourceNotFoundException;
import com.apiproject.repositories.admin.UserRepository;
import com.apiproject.security.JwtService;
import com.apiproject.services.admin.UserService;
import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private Cloudinary cloudinary;

    @Mock
    private Uploader uploader;

    @Mock
    private Cache cache;

    @InjectMocks
    private UserService userService;

    // Solo procura hacer los STUB con llamadas que si usaste en el metodo a probar
    @Test
    void debera_retornar_un_response_de_un_usuario() throws IOException {
        // 1️⃣ ARRANGE
        Long userId = 5L;
        MultipartFile fileFalso = mock(MultipartFile.class);

        UserAdmin usuarioFalso = UserAdmin.builder()
                .id(userId)
                .email("manuel@example.com")
                .build();

        Map<String, Object> resultadoCloudinaryFalso = Map.of("secure_url", "https://cloudinary.com/foto123.jpg");

        when(fileFalso.getContentType()).thenReturn("image/png");
        when(fileFalso.getBytes()).thenReturn(new byte[]{1, 2, 3});
        when(userRepository.findById(userId)).thenReturn(Optional.of(usuarioFalso));
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(any(), any())).thenReturn(resultadoCloudinaryFalso);
        when(userRepository.save(any(UserAdmin.class))).thenReturn(usuarioFalso);

        UserResponseDTO resultado = userService.subirFotoPerfil(userId, fileFalso);

        assertNotNull(resultado);
        assertEquals("https://cloudinary.com/foto123.jpg", resultado.photo());

        verify(userRepository, times(1)).findById(userId);
        verify(uploader, times(1)).upload(any(), any());
        verify(userRepository, times(1)).save(usuarioFalso);
    }

    @Test
    void modifyData_usuarioExiste_debeActualizarSoloLosCamposNoNulos() {
        // 1️⃣ARRANGE: el usuario que YA existe en la "base de datos"
        UserAdmin existente = UserAdmin.builder()
                .id(1L)
                .fullName("Nombre Viejo")
                .email("viejo@example.com")
                .businessName("Negocio Viejo")
                .phone(3000000000L)
                .colorTypes(ColorTypes.AZUL)
                .build();

        // Los datos "nuevos" que llegan a actualizar (ojo: email viene null a propósito)
        // email, phone y colorTypes vienen null: no deberían cambiar
        UpdateAdminRequestDTO datosNuevos = new UpdateAdminRequestDTO(
                "Nombre Nuevo", null, null, "Negocio Nuevo", null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(userRepository.save(any(UserAdmin.class))).thenReturn(existente);

        // ACT
        UserResponseDTO resultado = userService.modifyData(1L, datosNuevos);

        // ASSERT: verifico el TIPO de retorno
        assertInstanceOf(UserResponseDTO.class, resultado);

        // ASSERT: verifico que solo cambió lo que SÍ venía con valor
        assertEquals("Nombre Nuevo", resultado.fullName());       // sí cambió
        assertEquals("Negocio Nuevo", resultado.businessName());  // sí cambió

        // Y estos deberían seguir igual (porque venían null en datosNuevos)
        assertEquals("viejo@example.com", existente.getEmail());
        assertEquals(3000000000L, existente.getPhone());
        assertEquals(ColorTypes.AZUL, existente.getColorTypes());

        // 5️⃣ VERIFY
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(existente);
    }

    @Test
    void modifyData_usuarioNoExiste_debeLanzarExcepcion() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        UpdateAdminRequestDTO datosNuevos = new UpdateAdminRequestDTO("Nombre Nuevo", null, null, null, null);

        assertThrows(ResourceNotFoundException.class, () -> {
            userService.modifyData(99L, datosNuevos);
        });

        // Confirmo que NUNCA se guardó nada, porque falló antes
        verify(userRepository, never()).save(any());
    }

    @Test
    void login_pegandoElHashComoContrasena_debeRechazarYNoTocarLaBD() {
        String hash = "$2a$10$abcdefghijklmnopqrstuuJ0123456789abcdefghijklmnopqrstu";
        UserAdmin admin = UserAdmin.builder().id(1L).email("admin@test.com").password(hash).build();

        when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(admin));
        when(passwordEncoder.matches(hash, hash)).thenReturn(false);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> userService.login("admin@test.com", hash));

        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
        verify(userRepository, never()).updatePasswordById(anyLong(), anyString());
        verify(jwtService, never()).generateAdminToken(anyLong(), anyString(), anyString());
    }

    private UserAdmin adminExistente() {
        return UserAdmin.builder().id(1L).fullName("Admin").email("admin@test.com").build();
    }

    @Test
    void modifyData_emailDeOtroAdmin_409YNoGuarda() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(adminExistente()));
        when(userRepository.existsByEmailAndIdNot("otro@test.com", 1L)).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                userService.modifyData(1L, new UpdateAdminRequestDTO(null, " Otro@Test.com ", null, null, null)));

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        verify(userRepository, never()).save(any());
    }

    @Test
    void modifyData_emailInvalido_400() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(adminExistente()));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                userService.modifyData(1L, new UpdateAdminRequestDTO(null, "no-es-email", null, null, null)));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        verify(userRepository, never()).save(any());
    }

    @Test
    void modifyData_nombreVacio_400() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(adminExistente()));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                userService.modifyData(1L, new UpdateAdminRequestDTO("   ", null, null, null, null)));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void modifyData_mismoEmailConOtrasMayusculas_noEsConflicto() {
        UserAdmin admin = adminExistente();
        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));

        UserResponseDTO r = userService.modifyData(1L, new UpdateAdminRequestDTO(null, "ADMIN@test.com", null, null, null));

        assertEquals("admin@test.com", r.email());
        verify(userRepository, never()).existsByEmailAndIdNot(anyString(), anyLong());
    }

}
