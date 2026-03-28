package com.example.apiproject.controllers.user.admin;

import com.example.apiproject.entities.admin.ProductImage;
import com.example.apiproject.services.user.admin.ProductImageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/product-images")
public class ProductImageController {

    private final ProductImageService productImageService;

    public ProductImageController(ProductImageService productImageService) {
        this.productImageService = productImageService;
    }

    @PostMapping("/upload/{productId}")
    public ResponseEntity<ProductImage> uploadImage(
            @PathVariable Long productId,
            @RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(productImageService.uploadImage(productId, file));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<List<ProductImage>> getImages(@PathVariable Long productId) {
        return ResponseEntity.ok(productImageService.getImagesByProductId(productId));
    }

    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> deleteImage(@PathVariable Long imageId) throws IOException {
        productImageService.deleteImage(imageId);
        return ResponseEntity.noContent().build();
    }
}