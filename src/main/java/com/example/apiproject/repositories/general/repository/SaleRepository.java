package com.example.apiproject.repositories.general.repository;

import com.example.apiproject.entities.general.entities.Sale;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {
    @NonNull
    Optional<Sale> findById(@NonNull Long id);
}