package com.apiproject.security;

import jakarta.servlet.Filter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

/**
 * Reglas de acceso de {@code SecurityConfig} con la cadena de filtros real y JWT reales.
 * Solo se mira si la seguridad deja pasar (status != 401/403) o corta (401/403);
 * lo que responda el controller despues (200, 404, 400...) no importa aqui.
 */
@SpringBootTest
class SecurityRulesTest {

    @Autowired private WebApplicationContext context;
    @Autowired @Qualifier("springSecurityFilterChain") private Filter springSecurityFilterChain;
    @Autowired private JwtService jwtService;

    private MockMvc mvc;
    private String adminToken;
    private String clientToken;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context).addFilters(springSecurityFilterChain).build();
        adminToken = jwtService.generateAdminToken(900_001L, "admin-test@example.com", "ADMIN");
        clientToken = jwtService.generateClientToken(900_002L, "client-test@example.com", "CLIENT");
    }

    private int status(RequestBuilder request) throws Exception {
        MvcResult result = mvc.perform(request).andReturn();
        return result.getResponse().getStatus();
    }

    private MockHttpServletRequestBuilder as(MockHttpServletRequestBuilder request, String token) {
        return request.header("Authorization", "Bearer " + token);
    }

    private void assertAllowed(RequestBuilder request) throws Exception {
        int status = status(request);
        assertTrue(status != 401 && status != 403, "Esperaba pasar la seguridad y fue " + status);
    }

    // ===== Catalogo publico (sin token) =====

    @Test
    void catalogoPublico_sinToken_pasa() throws Exception {
        for (String url : new String[]{
                "/api/product/search/active-with-images",
                "/api/product/activeProducts",
                "/api/product/search/id/1",
                "/api/product/search/category/ropa",
                "/api/product/1/related",
                "/api/client/1/admin",
                "/api/services/catalog/1",
                "/api/product-reviews/product/1",
                "/api/product-reviews/product/1/summary",
                "/api/product-images/1",
                "/actuator/health"
        }) {
            int status = status(get(url));
            assertTrue(status != 401 && status != 403, url + " deberia ser publico y fue " + status);
        }
    }

    // ===== Escrituras de producto =====

    @Test
    void guardarProducto_sinToken_401_yComoCliente_403() throws Exception {
        assertEquals(401, status(post("/api/product/saveProduct").contentType(MediaType.APPLICATION_JSON).content("{}")));
        assertEquals(403, status(as(post("/api/product/saveProduct"), clientToken)
                .contentType(MediaType.APPLICATION_JSON).content("{}")));
    }

    @Test
    void reordenarImagenes_soloAdmin() throws Exception {
        assertEquals(401, status(patch("/api/product-images/reorder")));
        assertEquals(403, status(as(patch("/api/product-images/reorder"), clientToken)));
        assertEquals(403, status(as(patch("/api/sh-product-images/reorder"), clientToken)));
        assertAllowed(as(patch("/api/product-images/reorder"), adminToken)
                .contentType(MediaType.APPLICATION_JSON).content("{}"));
    }

    // ===== Pantallas de admin =====

    @Test
    void dashboardYMetricas_comoCliente_403() throws Exception {
        assertEquals(403, status(as(get("/dashboard-controller/get-data-dashboard"), clientToken)));
        assertEquals(403, status(as(get("/api/sales-items/show-with-limits"), clientToken)));
        assertEquals(403, status(as(get("/actuator/prometheus"), clientToken)));
        assertEquals(401, status(get("/actuator/prometheus")));
    }

    @Test
    void metricas_comoAdmin_pasa() throws Exception {
        assertAllowed(as(get("/actuator/prometheus"), adminToken));
    }

    // ===== Compras =====

    @Test
    void comprar_comoAdmin_403_ySinToken_401() throws Exception {
        assertEquals(403, status(as(post("/api/sale/purchase"), adminToken)
                .contentType(MediaType.APPLICATION_JSON).content("{}")));
        assertEquals(401, status(post("/api/sale/purchase").contentType(MediaType.APPLICATION_JSON).content("{}")));
    }

    // ===== Cupones =====

    @Test
    void misCupones_comoCliente_pasa_peroGestion_403() throws Exception {
        assertAllowed(as(get("/api/cupons/assignments/my"), clientToken));
        assertAllowed(as(get("/api/sh-cupons/assignments/my"), clientToken));
        assertAllowed(as(get("/api/services-cupons/assignments/my"), clientToken));
        assertEquals(403, status(as(get("/api/cupons"), clientToken)));
        assertEquals(403, status(as(post("/api/cupons/send"), clientToken)));
    }

    // ===== Token invalido =====

    @Test
    void tokenFalso_401() throws Exception {
        assertEquals(401, status(as(get("/api/cupons/assignments/my"), "no-es-un-jwt")));
    }
}
