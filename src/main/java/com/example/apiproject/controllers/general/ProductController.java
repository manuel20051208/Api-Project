package com.example.apiproject.controllers.general;

import com.example.apiproject.entities.general.entities.Product;
import com.example.apiproject.services.general.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
public class ProductController {
    public final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("search/id/{id}")
    public ResponseEntity<List<Product>> getProductById(@PathVariable Long id) {
        List<Product> products = productService.findAllById(id);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/search/category/{category}")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable String category) {
        List<Product> search = productService.findAllByCategoryIgnoreCase(category);
        return ResponseEntity.ok(search);
    }

    @GetMapping("/search/name/{name}")
    public Product getProduct(@PathVariable String name) {
        return productService.findByNameIgnoreCase(name);
    }

    @GetMapping("/saveProduct")
    public ResponseEntity<Product> saveProduct(Product product) {
        productService.save(product);
        return ResponseEntity.ok(product);
    }

    @PostMapping("/deleteSafe")
    public ResponseEntity<Product> deleteProduct(Product product) {
        productService.deleteProductSafe(product.getId());
        return ResponseEntity.ok(product);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable long id, @RequestBody Product product) {
        Product findIt = productService.updateProduct(id, product);
        return ResponseEntity.ok(findIt);
    }

    @GetMapping("/activeProducts")
    public List<Product> getActiveProducts() {
        return productService.findByActiveTrue();
    }

    @GetMapping("/all")
    public List<Product> getAllProductsDashboard() {
        return productService.findAll();
    }
}
