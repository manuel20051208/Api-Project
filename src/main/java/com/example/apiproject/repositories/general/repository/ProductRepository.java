package com.example.apiproject.repositories.general.repository;

import com.example.apiproject.entities.general.entities.Product;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByNameIgnoreCase(String name);
    List<Product> findAllByCategoryIgnoreCase(String category);
    List<Product> findAllById(Long id);
    List<Product> findByActiveTrue();

    @Transactional
    @Modifying
    void deleteById(@NonNull Long id);
}