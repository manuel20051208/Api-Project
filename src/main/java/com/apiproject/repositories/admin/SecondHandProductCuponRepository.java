package com.apiproject.repositories.admin;

import com.apiproject.entities.admin.SecondHandProductCuponsApplied;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SecondHandProductCuponRepository extends JpaRepository<SecondHandProductCuponsApplied, Long> {

    @Query(value = """
            SELECT sh_product_id FROM secondhand_product_cupons_applied
            WHERE sh_cupons_id = :cuponId
            ORDER BY sh_product_id
            """, nativeQuery = true)
    List<Long> findShProductIdsByCuponId(@Param("cuponId") Long cuponId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM secondhand_product_cupons_applied WHERE sh_cupons_id = :cuponId", nativeQuery = true)
    int deleteByCuponId(@Param("cuponId") Long cuponId);
}
