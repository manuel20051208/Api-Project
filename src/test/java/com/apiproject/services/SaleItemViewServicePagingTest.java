package com.apiproject.services;

import com.apiproject.repositories.admin.SaleItemViewRepository;
import com.apiproject.services.admin.SaleItemViewService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/** show-with-no-restrinction pagina de verdad y ambos listados tienen tope de tamaño. */
@ExtendWith(MockitoExtension.class)
class SaleItemViewServicePagingTest {

    private static final long ADMIN_ID = 3L;

    @Mock private SaleItemViewRepository repository;
    @InjectMocks private SaleItemViewService service;

    private HttpStatus statusOf(Runnable call) {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, call::run);
        return HttpStatus.valueOf(ex.getStatusCode().value());
    }

    @Test
    void historial_usaLaPaginaYElTamanoPedidos() {
        when(repository.findAll(eq(ADMIN_ID), any(Pageable.class))).thenReturn(Page.empty());

        service.showEverythingWithNoRestriction(ADMIN_ID, 3, 50);

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(repository).findAll(eq(ADMIN_ID), captor.capture());
        assertEquals(3, captor.getValue().getPageNumber()); // antes siempre 0
        assertEquals(50, captor.getValue().getPageSize());  // antes siempre 200
        assertTrue(captor.getValue().getSort().isSorted()); // mas recientes primero
    }

    @Test
    void historial_paginaNegativa_400() {
        assertEquals(HttpStatus.BAD_REQUEST, statusOf(() -> service.showEverythingWithNoRestriction(ADMIN_ID, -1, 20)));
        verifyNoInteractions(repository);
    }

    @Test
    void historial_tamanoFueraDeRango_400() {
        assertEquals(HttpStatus.BAD_REQUEST, statusOf(() -> service.showEverythingWithNoRestriction(ADMIN_ID, 0, 0)));
        assertEquals(HttpStatus.BAD_REQUEST,
                statusOf(() -> service.showEverythingWithNoRestriction(ADMIN_ID, 0, SaleItemViewService.MAX_PAGE_SIZE + 1)));
        verifyNoInteractions(repository);
    }

    @Test
    void historial_tamanoMaximoPermitido_ok() {
        when(repository.findAll(eq(ADMIN_ID), any(Pageable.class))).thenReturn(Page.empty());

        assertDoesNotThrow(() -> service.showEverythingWithNoRestriction(ADMIN_ID, 0, SaleItemViewService.MAX_PAGE_SIZE));
    }

    @Test
    void conLimites_sizePageSinTope_400() {
        assertEquals(HttpStatus.BAD_REQUEST, statusOf(() -> service.showEverythingWithLimits(ADMIN_ID, 1_000_000)));
        assertEquals(HttpStatus.BAD_REQUEST, statusOf(() -> service.showEverythingWithLimits(ADMIN_ID, 0)));
        verifyNoInteractions(repository);
    }
}
