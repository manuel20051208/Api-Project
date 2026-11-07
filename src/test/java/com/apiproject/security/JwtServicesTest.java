package com.apiproject.security;

import com.apiproject.entities.admin.UserAdmin;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

public class JwtServicesTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        // Inyección manual: el test no levanta el contexto de Spring
        ReflectionTestUtils.setField(jwtService, "secretKey",
                Base64.getEncoder().encodeToString(
                        "mi-llave-secreta-de-32-bytes-minimo!!".getBytes()));
        ReflectionTestUtils.setField(jwtService, "expirationMs", 3600000L);
    }

    @Test
    void it_must_return_token(){
        Long userId = 5L;

        UserAdmin usuarioFalso = UserAdmin.builder()
                .id(userId)
                .email("manuel@example.com")
                .build();

        String token = jwtService.generateAdminToken(userId, usuarioFalso.getEmail(), "ADMIN");
        assertInstanceOf(String.class, token);
        assertEquals(token, jwtService.generateAdminToken(5L, "manuel@example.com", "ADMIN"));
    }

    @Test
    void it_must_not_return_a_token_if_at_least_one_or_more_parameter_is_empty(){
        assertThrows(JwtException.class, () -> jwtService.generateAdminToken(5L, null, "ADMIN"));
        assertThrows(JwtException.class, () -> jwtService.generateAdminToken(5L, "", "ADMIN"));
        assertThrows(JwtException.class, () -> jwtService.generateAdminToken(5L, "  ", "ADMIN"));

        assertThrows(JwtException.class, () -> jwtService.generateAdminToken(null, "manuel@example.com", "ADMIN"));
        assertThrows(JwtException.class, () -> jwtService.generateAdminToken(0L, "manuel@example.com", "ADMIN"));
        assertThrows(JwtException.class, () -> jwtService.generateAdminToken(-1L, "manuel@example.com", "ADMIN"));

        assertThrows(JwtException.class, () -> jwtService.generateAdminToken(5L, "manuel@example.com", null));
        assertThrows(JwtException.class, () -> jwtService.generateAdminToken(5L, "manuel@example.com", ""));
        assertThrows(JwtException.class, () -> jwtService.generateAdminToken(5L, "manuel@example.com", "  "));
    }
}
