package com.example.apiproject.controllers.admin;

import com.example.apiproject.DTOs.Admin.ProductImageDTO;
import com.example.apiproject.services.user.admin.ProductImageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

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
            @RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(productImageService.uploadImage(productId, file));
    }

    @Operation(summary = "Get all images for a product")
    @GetMapping("/{productId}")
    public ResponseEntity<List<ProductImageDTO>> getImages(@PathVariable Long productId) {
        return ResponseEntity.ok(productImageService.getImagesByProductId(productId));
    }

    @Operation(summary = "Delete a product image")
    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> deleteImage(@PathVariable Long imageId) throws IOException {
        productImageService.deleteImage(imageId);
        return ResponseEntity.noContent().build();
    }
}