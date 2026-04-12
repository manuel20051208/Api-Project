package com.example.apiproject.repositories.admin;

import com.example.apiproject.entities.admin.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    long countByProductId(Long productId);

    List<ProductImage> findByProductIdOrderByDisplayOrder(Long productId);
}