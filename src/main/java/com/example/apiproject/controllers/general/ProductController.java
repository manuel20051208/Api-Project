package com.example.apiproject.controllers.general;

import com.example.apiproject.DTOs.General.ProductResponseDTO;
import com.example.apiproject.entities.general.entities.Product;
import com.example.apiproject.services.general.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "Product", description = "Product Management Endpoints")
@RestController
@RequestMapping("/api/product")
public class ProductController {
    public final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "Get product by ID")
    @GetMapping("search/id/{id}")
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

    @Operation(summary = "Save a new product")
    @GetMapping("/saveProduct")
    public ResponseEntity<Product> saveProduct(Product product) {
        productService.save(product);
        return ResponseEntity.ok(product);
    }

    @Operation(summary = "Soft delete a product")
    @PostMapping("/deleteSafe")
    public ResponseEntity<Product> deleteProduct(Product product) {
        productService.deleteProductSafe(product.getId());
        return ResponseEntity.ok(product);
    }

    @Operation(summary = "Update product details")
    @PutMapping("/update/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(@PathVariable long id, @RequestBody Product product) {
        ProductResponseDTO findIt = productService.updateProduct(id, product);
        return ResponseEntity.ok(findIt);
    }

    @Operation(summary = "Get active products (paginated)")
    @GetMapping("/activeProducts")
    public Page<ProductResponseDTO> getActiveProducts(@RequestParam int sizePage) {
        return productService.findByActiveTrue(sizePage);
    }
}