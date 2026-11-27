package com.apiproject.services;

import com.apiproject.DTOs.Admin.ProductImageDTO;
import com.apiproject.DTOs.Admin.ReorderImagesRequestDTO;
import com.apiproject.entities.admin.ProductImage;
import com.apiproject.repositories.admin.ProductImageRepository;
import com.apiproject.services.admin.ProductImageService;
import com.apiproject.services.general.ProductService;
import com.cloudinary.Cloudinary;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/** Reordenar imagenes y tope por producto (la subida no toca Cloudinary si falla una regla). */
@ExtendWith(MockitoExtension.class)
class ProductImageServiceReorderTest {

    private static final long ADMIN_ID = 5L;
    private static final long PRODUCT_ID = 10L;

    @Mock private ProductImageRepository productImageRepository;
    @Mock private ProductService productService;
    @Mock private Cloudinary cloudinary;
    @InjectMocks private ProductImageService service;

    private static ProductImage image(long id, long order) {
        ProductImage image = new ProductImage();
        image.setId(id);
        image.setDisplayOrder(order);
        image.setFileName("img" + id + ".png");
        return image;
    }

    private static HttpStatus statusOf(Runnable call) {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, call::run);
        return HttpStatus.valueOf(ex.getStatusCode().value());
    }

    @Test
    void dueno_reordenaYAsignaDisplayOrderEnElOrdenPedido() {
        when(productImageRepository.findByProductIdOrderByDisplayOrder(PRODUCT_ID))
                .thenReturn(List.of(image(1, 1), image(2, 2), image(3, 3)));

        List<ProductImageDTO> result = service.reorder(new ReorderImagesRequestDTO(PRODUCT_ID, List.of(3L, 1L, 2L)), ADMIN_ID);

        assertEquals(List.of(3L, 1L, 2L), result.stream().map(ProductImageDTO::id).toList());
        assertEquals(List.of(1L, 2L, 3L), result.stream().map(ProductImageDTO::displayOrder).toList());
        verify(productService).findUser(PRODUCT_ID, ADMIN_ID);
        verify(productImageRepository).saveAll(anyList());
        verify(productService).evictProductCaches(PRODUCT_ID, ADMIN_ID);
    }

    @Test
    void noDueno_403_sinTocarImagenes() {
        when(productService.findUser(PRODUCT_ID, ADMIN_ID))
                .thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "no"));

        assertEquals(HttpStatus.FORBIDDEN,
                statusOf(() -> service.reorder(new ReorderImagesRequestDTO(PRODUCT_ID, List.of(1L)), ADMIN_ID)));
        verify(productImageRepository, never()).saveAll(anyList());
    }

    @Test
    void idsQueNoCuadran_400() {
        when(productImageRepository.findByProductIdOrderByDisplayOrder(PRODUCT_ID))
                .thenReturn(List.of(image(1, 1), image(2, 2)));

        // falta una, sobra una ajena y repetida
        for (List<Long> ids : List.of(List.of(1L), List.of(1L, 2L, 99L), List.of(1L, 1L))) {
            assertEquals(HttpStatus.BAD_REQUEST,
                    statusOf(() -> service.reorder(new ReorderImagesRequestDTO(PRODUCT_ID, ids), ADMIN_ID)));
        }
        verify(productImageRepository, never()).saveAll(anyList());
    }

    @Test
    void tope_409_antesDeSubirACloudinary() {
        ReflectionTestUtils.setField(service, "maxImagesPerProduct", 3);
        when(productImageRepository.countByProductId(PRODUCT_ID)).thenReturn(3L);
        MockMultipartFile file = new MockMultipartFile("file", "a.png", "image/png", new byte[]{1, 2, 3});

        assertEquals(HttpStatus.CONFLICT, statusOf(() -> {
            try {
                service.uploadImage(PRODUCT_ID, file, ADMIN_ID);
            } catch (java.io.IOException e) {
                throw new RuntimeException(e);
            }
        }));
        verifyNoInteractions(cloudinary);
        verify(productImageRepository, never()).save(any());
    }
}
