package com.example.apiproject.services.general.service;

import com.example.apiproject.DTOs.General.ProductResponseDTO;
import com.example.apiproject.entities.general.entities.Product;
import com.example.apiproject.repositories.general.ProductRepository;
import io.micrometer.common.lang.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {
    public final ProductRepository productRepository;

    public List<ProductResponseDTO> findAllByCategoryIgnoreCase(String category) {
        return productRepository.findAllByCategoryIgnoreCase(category).stream()
                .map(ProductResponseDTO::fromEntity)
                .toList();
    }

    public List<ProductResponseDTO> findAllById(Long id) {
        return productRepository.findAllById(id).stream()
                .map(ProductResponseDTO::fromEntity)
                .toList();
    }

    public ProductResponseDTO findByNameIgnoreCase(String name) {
        Product product = productRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new RuntimeException("Didn't find the field: "));
        return ProductResponseDTO.fromEntity(product);
    }

    public void save(Product product) {
        productRepository.save(product);
    }

    public Page<ProductResponseDTO> findByActiveTrue(int pageSize) {
        Pageable limitTen = PageRequest.of(0, pageSize);
        Page<Product> page = productRepository.findByActiveTrue(limitTen);
        return page.map(ProductResponseDTO::fromEntity);
    }

    public Product findUser(Long id){
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public ProductResponseDTO updateProduct(Long id, @NonNull Product product) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Optional.ofNullable(product.getId()).ifPresent(existing::setId);
        Optional.ofNullable(product.getName()).ifPresent(existing::setName);
        Optional.ofNullable(product.getPrice()).ifPresent(existing::setPrice);
        Optional.ofNullable(product.getStock()).ifPresent(existing::setStock);
        Optional.ofNullable(product.getCategory()).ifPresent(existing::setCategory);

        return ProductResponseDTO.fromEntity(productRepository.save(existing));
    }

    public void deleteProductSafe(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product no found"));
        product.setActive(false);
        productRepository.save(product);
    }
}