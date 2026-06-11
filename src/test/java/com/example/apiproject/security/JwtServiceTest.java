package com.example.apiproject.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey",
                Base64.getEncoder().encodeToString(
                        "mi-llave-secreta-de-32-bytes-minimo!!".getBytes()));
        ReflectionTestUtils.setField(jwtService, "expirationMs", 3600000L);
    }

    @Test
    void generateToken_debeRetornarTokenValido() {
        String token = jwtService.generateToken(1L, "manuel", "ADMIN");
        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void parseAuthenticatedUser_tokenValido_debeRetornarUsuario() {
        String token = jwtService.generateToken(1L, "manuel", "ADMIN");
        Optional<AuthenticatedUser> result = jwtService.parseAuthenticatedUser(token);
        assertTrue(result.isPresent());
        assertEquals("manuel", result.get().getUsername());
    }

    @Test
    void parseAuthenticatedUser_tokenInvalido_debeRetornarEmpty() {
        Optional<AuthenticatedUser> result = jwtService.parseAuthenticatedUser("token.falso.bro");
        assertTrue(result.isEmpty());
    }

    @Test
    void parseAuthenticatedUser_tokenExpirado_debeRetornarEmpty() {
        ReflectionTestUtils.setField(jwtService, "expirationMs", -1000L);
        String token = jwtService.generateToken(1L, "manuel", "ADMIN");
        Optional<AuthenticatedUser> result = jwtService.parseAuthenticatedUser(token);
        assertTrue(result.isEmpty());
    }
}