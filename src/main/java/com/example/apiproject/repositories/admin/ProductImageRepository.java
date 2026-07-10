package com.example.apiproject.repositories.admin;

import com.example.apiproject.entities.admin.ProductImage;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    @Query(value = "SELECT COUNT(*) FROM product_image WHERE product_id = :productId", nativeQuery = true)
    long countByProductId(@Param("productId") Long productId);

    @Query(value = "SELECT * FROM product_image WHERE product_id = :productId ORDER BY display_order", nativeQuery = true)
    List<ProductImage> findByProductIdOrderByDisplayOrder(@Param("productId") Long productId);

    @Query(value = "SELECT * FROM product_image WHERE id = :imageId", nativeQuery = true)
    Optional<ProductImage> findDeleteInfoById(@Param("imageId") Long imageId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM product_image WHERE id = :imageId", nativeQuery = true)
    void deleteByImageId(@Param("imageId") Long imageId);
}
