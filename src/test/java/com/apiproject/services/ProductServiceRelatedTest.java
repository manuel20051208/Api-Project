package com.apiproject.services;

import com.apiproject.DTOs.General.ProductResponseDTO;
import com.apiproject.entities.admin.UserAdmin;
import com.apiproject.entities.general.Product;
import com.apiproject.exceptions.ResourceNotFoundException;
import com.apiproject.repositories.general.ProductRepository;
import com.apiproject.services.general.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/** GET /api/product/{id}/related: misma categoria + mismo admin, sin el propio producto. */
@ExtendWith(MockitoExtension.class)
class ProductServiceRelatedTest {

    private static final long ADMIN_ID = 7L;

    @Mock private ProductRepository productRepository;
    @InjectMocks private ProductService service;

    private static Product product(long id, String name) {
        Product p = new Product();
        p.setId(id);
        p.setName(name);
        p.setCategory("Ropa");
        p.setActive(true);
        p.setUserAdmin(UserAdmin.builder().id(ADMIN_ID).build());
        return p;
    }

    @Test
    void productoInexistente_404() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.findRelated(99L, 8));
    }

    @Test
    void limiteFueraDeRango_400_sinTocarLaBase() {
        for (int limit : new int[]{0, -1, ProductService.RELATED_MAX_LIMIT + 1}) {
            ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                    () -> service.findRelated(1L, limit));
            assertEquals(HttpStatus.BAD_REQUEST.value(), ex.getStatusCode().value());
        }
        verifyNoInteractions(productRepository);
    }

    @Test
    void buscaPorCategoriaYAdminExcluyendoElPropio_yRespetaElOrdenDeIds() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product(1L, "Origen")));
        when(productRepository.findRelatedProductIds(eq("Ropa"), eq(ADMIN_ID), eq(1L), any(Pageable.class)))
                .thenReturn(List.of(3L, 2L));
        // la query con JOIN FETCH puede devolver otro orden: se reordena segun los ids
        when(productRepository.findAllWithImagesByIdIn(List.of(3L, 2L)))
                .thenReturn(List.of(product(2L, "B"), product(3L, "A")));

        List<ProductResponseDTO> result = service.findRelated(1L, 5);

        assertEquals(List.of(3L, 2L), result.stream().map(ProductResponseDTO::id).toList());
        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(productRepository).findRelatedProductIds(eq("Ropa"), eq(ADMIN_ID), eq(1L), captor.capture());
        assertEquals(5, captor.getValue().getPageSize());
        assertEquals(0, captor.getValue().getPageNumber());
    }

    @Test
    void sinRelacionados_listaVacia() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product(1L, "Origen")));
        when(productRepository.findRelatedProductIds(any(), any(), any(), any(Pageable.class))).thenReturn(List.of());

        assertTrue(service.findRelated(1L, 8).isEmpty());
        verify(productRepository, never()).findAllWithImagesByIdIn(any());
    }
}
