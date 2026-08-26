package com.apiproject.repositories.general;

import com.apiproject.entities.general.SecondHandProduct;
import com.apiproject.repositories.projection.ShProductCardProjection;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SecondHandProductRepository extends JpaRepository<SecondHandProduct, Long> {

    @Query(value = """
            SELECT p.id,
                   p.name,
                   p.price,
                   p.stock,
                   p.category,
                   p.description,
                   p.active,
                   p.time_of_use,
                   p.level_of_secondhand_product,
                   COALESCE(u.business_name, u.full_name) AS owner_name,
                   i.url AS image_url
            FROM secondhand_product p
                     JOIN users u ON u.id = p.id_users
                     LEFT JOIN LATERAL (
                         SELECT si.url
                         FROM secondhand_product_images si
                         WHERE si.product_id = p.id
                           AND si.url IS NOT NULL
                         ORDER BY si.display_order
                         LIMIT 1
                     ) i ON true
            WHERE p.active = TRUE
              AND (:category IS NULL OR LOWER(p.category) = LOWER(:category))
              AND (:search IS NULL OR LOWER(p.name) LIKE '%' || LOWER(:search) || '%')
            ORDER BY p.id DESC
            """, nativeQuery = true)
    List<ShProductCardProjection> findActiveCatalogCards(
            @Param("category") String category,
            @Param("search") String search);

    @Query(value = """
            SELECT p.id,
                   p.name,
                   p.price,
                   p.stock,
                   p.category,
                   p.description,
                   p.active,
                   p.time_of_use,
                   p.level_of_secondhand_product,
                   COALESCE(u.business_name, u.full_name) AS owner_name,
                   i.url AS image_url
            FROM secondhand_product p
                     JOIN users u ON u.id = p.id_users
                     LEFT JOIN LATERAL (
                         SELECT si.url
                         FROM secondhand_product_images si
                         WHERE si.product_id = p.id
                           AND si.url IS NOT NULL
                         ORDER BY si.display_order
                         LIMIT 1
                     ) i ON true
            WHERE p.id_users = :adminId
              AND (:search IS NULL OR LOWER(p.name) LIKE '%' || LOWER(:search) || '%')
            ORDER BY p.id DESC
            """, nativeQuery = true)
    List<ShProductCardProjection> findAdminCards(
            @Param("adminId") Long adminId,
            @Param("search") String search);

    @Query(value = "SELECT * FROM secondhand_product WHERE id IN (:ids) FOR UPDATE", nativeQuery = true)
    List<SecondHandProduct> findAllByIdInForUpdate(@Param("ids") List<Long> ids);

    @Query(value = "SELECT * FROM secondhand_product WHERE id_users = :adminId ORDER BY id DESC", nativeQuery = true)
    List<SecondHandProduct> findAllByOwner(@Param("adminId") Long adminId);

    @Query(value = "SELECT COUNT(*) > 0 FROM secondhand_product WHERE id = :id AND id_users = :adminId", nativeQuery = true)
    boolean existsByIdAndOwner(@Param("id") Long id, @Param("adminId") Long adminId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE secondhand_product SET stock = stock - :quantity WHERE id = :id AND stock >= :quantity", nativeQuery = true)
    int decrementStock(@Param("id") Long id, @Param("quantity") Integer quantity);
}
