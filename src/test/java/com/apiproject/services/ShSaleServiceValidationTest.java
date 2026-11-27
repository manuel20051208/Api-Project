package com.apiproject.services;

import com.apiproject.DTOs.General.ShPurchaseItemRequestDTO;
import com.apiproject.DTOs.General.ShPurchaseRequestDTO;
import com.apiproject.services.general.ShSaleService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Validacion del body de la compra SH: debe fallar con 4xx antes de tocar repositorios. */
@ExtendWith(MockitoExtension.class)
class ShSaleServiceValidationTest {

    private static final long AUTH_CLIENT_ID = 5L;
    private static final List<ShPurchaseItemRequestDTO> ITEMS = List.of(new ShPurchaseItemRequestDTO(1L, 1));

    // Sin mocks: si la validacion dejara pasar el request, el primer acceso a un repo daria NPE.
    @InjectMocks private ShSaleService shSaleService;

    private HttpStatus statusOf(ShPurchaseRequestDTO request) {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> shSaleService.purchase(request, AUTH_CLIENT_ID));
        return HttpStatus.valueOf(ex.getStatusCode().value());
    }

    @Test
    void sinClientId_400_yNoNPE() {
        assertEquals(HttpStatus.BAD_REQUEST, statusOf(new ShPurchaseRequestDTO(null, null, ITEMS)));
    }

    @Test
    void clientIdDeOtroCliente_403() {
        assertEquals(HttpStatus.FORBIDDEN, statusOf(new ShPurchaseRequestDTO(99L, null, ITEMS)));
    }

    @Test
    void sinItems_400() {
        assertEquals(HttpStatus.BAD_REQUEST, statusOf(new ShPurchaseRequestDTO(AUTH_CLIENT_ID, null, List.of())));
    }
}
