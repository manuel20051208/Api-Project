package com.example.apiproject.repositories.general;

import com.example.apiproject.entities.general.entities.Product;
import io.micrometer.common.lang.NonNull;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByNameIgnoreCase(String name);
    List<Product> findAllByCategoryIgnoreCase(String category);
    List<Product> findAllById(Long id);

    Page<Product> findByActiveTrue(Pageable pageable);

    @Transactional
    @Modifying
    void deleteById(@NonNull Long id);
}