package com.apiproject.controllers.admin;

import com.apiproject.DTOs.Admin.ShProductImageDTO;
import com.apiproject.security.AuthenticatedUser;
import com.apiproject.services.admin.SecondHandProductImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Tag(name = "Second Hand Product Image", description = "Upload/list/delete de imagenes de productos de segunda mano")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sh-product-images")
public class SecondHandProductImageController {
    private final SecondHandProductImageService secondHandProductImageService;

    @Operation(summary = "Subir una imagen para un producto de segunda mano (ADMIN)")
    @PostMapping("/upload/{productId}")
    public ResponseEntity<ShProductImageDTO> uploadImage(
            @PathVariable Long productId,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser) throws IOException {
        return ResponseEntity.ok(secondHandProductImageService.uploadImage(productId, file, authenticatedUser.id()));
    }

    @Operation(summary = "Imagenes de un producto de segunda mano")
    @GetMapping("/{productId}")
    public ResponseEntity<List<ShProductImageDTO>> getImages(@PathVariable Long productId) {
        return ResponseEntity.ok(secondHandProductImageService.getImagesByProductId(productId));
    }

    @Operation(summary = "Eliminar una imagen de producto de segunda mano (ADMIN)")
    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> deleteImage(
            @PathVariable Long imageId,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser) throws IOException {
        secondHandProductImageService.deleteImage(imageId, authenticatedUser.id());
        return ResponseEntity.noContent().build();
    }
}
