package com.example.apiproject.controllers.general;

import com.example.apiproject.DTOs.General.ProductResponseDTO;
import com.example.apiproject.entities.general.Product;
import com.example.apiproject.security.AuthenticatedUser;
import com.example.apiproject.services.general.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "Product", description = "Product Management Endpoints")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/product")
public class ProductController {
    public final ProductService productService;

    @Operation(summary = "show everything by user authenticated")
    @GetMapping("/search")
    public List<ProductResponseDTO> getAll(@AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        return productService.findAll(authenticatedUser.id());
    }

    @Operation(summary = "Active products with at least one image (catalog / filter; single query, no N+1)")
    @GetMapping("/search/active-with-images")
    public List<ProductResponseDTO> getActiveProductsWithImages() {
        return productService.findAllActiveWithImages();
    }

    @Operation(summary = "Get product by ID")
    @GetMapping("/search/id/{id}")
    public ResponseEntity<List<ProductResponseDTO>> getProductById(@PathVariable Long id) {
        List<ProductResponseDTO> products = productService.findAllById(id);
        return ResponseEntity.ok(products);
    }

    @Operation(summary = "Get products by category")
    @GetMapping("/search/category/{category}")
    public ResponseEntity<List<ProductResponseDTO>> getProductsByCategory(@PathVariable String category) {
        List<ProductResponseDTO> search = productService.findAllByCategoryIgnoreCase(category);
        return ResponseEntity.ok(search);
    }

    @Operation(summary = "Get product by exact name")
    @GetMapping("/search/name/{name}")
    public ProductResponseDTO getProduct(@PathVariable String name) {
        return productService.findByNameIgnoreCase(name);
    }

    @Operation(summary = "Save a new product (query params; short payloads)")
    @PostMapping("/saveProduct")
    public ResponseEntity<Product> saveProduct(
            Product product,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        productService.save(product, authenticatedUser.id());
        return ResponseEntity.ok(product);
    }

    @Operation(summary = "Save a new product (JSON body; supports long description)")
    @PostMapping(value = "/saveProduct", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Product saveProductJson(
            @RequestBody Product product,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        productService.save(product, authenticatedUser.id());
        return product;
    }

    @Operation(summary = "Soft delete a product")
    @PostMapping("/deleteSafe")
    public ResponseEntity<Product> deleteProduct(
            Product product,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        productService.deleteProductSafe(product.getId(), authenticatedUser.id());
        return ResponseEntity.ok(product);
    }

    @Operation(summary = "Update product details")
    @PutMapping("/update/{id}")
    public ProductResponseDTO updateProduct(
            @PathVariable long id,
            @RequestBody Product product,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        return productService.updateProduct(id, product, authenticatedUser.id());
    }

    @Operation(summary = "Get active products (paginated)")
    @GetMapping("/activeProducts")
    public Page<ProductResponseDTO> getActiveProducts(@RequestParam(required = false) int sizePage) {
        return productService.findByActiveTrue(sizePage);
    }
}
