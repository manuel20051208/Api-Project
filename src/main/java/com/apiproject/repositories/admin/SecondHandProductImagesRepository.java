package com.apiproject.repositories.admin;

import com.apiproject.entities.admin.SecondHandProductImage;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SecondHandProductImagesRepository extends JpaRepository<SecondHandProductImage, Long> {

    @Query(value = "SELECT COUNT(*) FROM secondhand_product_images WHERE product_id = :productId", nativeQuery = true)
    long countByProductId(@Param("productId") Long productId);

    @Query(value = "SELECT * FROM secondhand_product_images WHERE product_id = :productId ORDER BY display_order", nativeQuery = true)
    List<SecondHandProductImage> findByProductIdOrderByDisplayOrder(@Param("productId") Long productId);

    @Query(value = "SELECT * FROM secondhand_product_images WHERE id = :imageId", nativeQuery = true)
    Optional<SecondHandProductImage> findDeleteInfoById(@Param("imageId") Long imageId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM secondhand_product_images WHERE id = :imageId", nativeQuery = true)
    void deleteByImageId(@Param("imageId") Long imageId);
}
