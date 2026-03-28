package com.example.apiproject.services.general.service;

import com.example.apiproject.entities.general.entities.Product;
import com.example.apiproject.repositories.general.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {
    public final ProductRepository productRepository;

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public List<Product> findAllByCategoryIgnoreCase(String category){
        return productRepository.findAllByCategoryIgnoreCase(category);
    }

    public List<Product> findAllById(Long id){
        return productRepository.findAllById(id);
    }

    public Product findByNameIgnoreCase(String name){
        return productRepository.findByNameIgnoreCase(name)
                .orElseThrow(()-> new RuntimeException("Didn't find the field: "));
    }

    public void save(Product product){
        productRepository.save(product);
    }

    public List<Product> findByActiveTrue(){
        return productRepository.findByActiveTrue();
    }

    public Product findById(Long id){
        return productRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Didn't find the field: "));
    }

    public Product updateProduct(Long id, @NonNull Product product){
        Product existing = findById(id);

        Optional.ofNullable(product.getId()).ifPresent(existing::setId);
        Optional.ofNullable(product.getName()).ifPresent(existing::setName);
        Optional.ofNullable(product.getPrice()).ifPresent(existing::setPrice);
        Optional.ofNullable(product.getStock()).ifPresent(existing::setStock);
        Optional.ofNullable(product.getCategory()).ifPresent(existing::setCategory);

        return productRepository.save(existing);
    }

    public void deleteProductSafe(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product no found"));
        product.setActive(false);
        productRepository.save(product);
    }
}