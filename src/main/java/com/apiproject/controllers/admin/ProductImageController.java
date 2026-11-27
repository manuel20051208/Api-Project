package com.apiproject.controllers.admin;

import com.apiproject.DTOs.Admin.ProductImageDTO;
import com.apiproject.security.AuthenticatedUser;
import com.apiproject.services.admin.ProductImageService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.apiproject.DTOs.Admin.ReorderImagesRequestDTO;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

import java.io.IOException;
import java.util.List;

@Tag(name = "Product Image", description = "Endpoints for uploading and deleting product images")
@RestController
@RequestMapping("/api/product-images")
public class ProductImageController {

    private final ProductImageService productImageService;

    public ProductImageController(ProductImageService productImageService) {
        this.productImageService = productImageService;
    }

    @Operation(summary = "Upload an image for a product")
    @PostMapping("/upload/{productId}")
    public ResponseEntity<ProductImageDTO> uploadImage(
            @PathVariable Long productId,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser) throws IOException {
        return ResponseEntity.ok(productImageService.uploadImage(productId, file, authenticatedUser.id()));
    }

    @Operation(summary = "Get all images for a product")
    @GetMapping("/{productId}")
    public ResponseEntity<List<ProductImageDTO>> getImages(@PathVariable Long productId) {
        return ResponseEntity.ok(productImageService.getImagesByProductId(productId));
    }

    @Operation(summary = "Delete a product image")
    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> deleteImage(
            @PathVariable Long imageId,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser) throws IOException {
        productImageService.deleteImage(imageId, authenticatedUser.id());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Reordenar las imagenes de un producto",
            description = "imageIds debe traer todas las imagenes del producto, sin repetir, en el nuevo orden (la primera es la portada).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Imagenes en el nuevo orden"),
            @ApiResponse(responseCode = "400", description = "Body invalido o imageIds no coincide con las imagenes del producto"),
            @ApiResponse(responseCode = "401", description = "Token ausente o invalido"),
            @ApiResponse(responseCode = "403", description = "El producto no es del admin autenticado")
    })
    @PatchMapping("/reorder")
    public ResponseEntity<List<ProductImageDTO>> reorder(
            @Valid @RequestBody ReorderImagesRequestDTO request,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        return ResponseEntity.ok(productImageService.reorder(request, authenticatedUser.id()));
    }
}
