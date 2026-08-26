package com.apiproject.repositories.admin;

import com.apiproject.entities.admin.ProductCuponApplied;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductCuponRepository extends JpaRepository<ProductCuponApplied, Long> {

    @Query(value = """
            SELECT product_id FROM product_cupons_applied
            WHERE cupons_id = :cuponId
            ORDER BY product_id
            """, nativeQuery = true)
    List<Long> findProductIdsByCuponId(@Param("cuponId") Long cuponId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM product_cupons_applied WHERE cupons_id = :cuponId", nativeQuery = true)
    int deleteByCuponId(@Param("cuponId") Long cuponId);
}
