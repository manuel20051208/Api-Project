package com.apiproject.repositories.general;

import com.apiproject.entities.general.ShSalesItem;
import com.apiproject.repositories.projection.ShSaleHistoryProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShSaleItemRepository extends JpaRepository<ShSalesItem, Long> {

    @Query(value = """
            SELECT si.id,
                   s.id AS sale_id,
                   sp.id AS product_id,
                   sp.name AS product_name,
                   si.quantity,
                   sp.price AS unit_price,
                   (COALESCE(si.quantity, 0)::NUMERIC * COALESCE(sp.price, 0)::NUMERIC)::DOUBLE PRECISION AS subtotal,
                   si.state,
                   COALESCE(si.date, s.created_at) AS date
            FROM sh_sales_item si
                     JOIN sh_sales s ON s.id = si.sh_sale_id
                     LEFT JOIN secondhand_product sp ON sp.id = si.sh_product_id
            WHERE si.client_id = :clientId
            ORDER BY COALESCE(si.date, s.created_at) DESC, si.id DESC
            """, nativeQuery = true)
    List<ShSaleHistoryProjection> findClientHistory(@Param("clientId") Long clientId);

    @Query(value = """
            SELECT si.id,
                   s.id AS sale_id,
                   sp.id AS product_id,
                   sp.name AS product_name,
                   si.quantity,
                   sp.price AS unit_price,
                   (COALESCE(si.quantity, 0)::NUMERIC * COALESCE(sp.price, 0)::NUMERIC)::DOUBLE PRECISION AS subtotal,
                   si.state,
                   COALESCE(si.date, s.created_at) AS date
            FROM sh_sales_item si
                     JOIN sh_sales s ON s.id = si.sh_sale_id
                     LEFT JOIN secondhand_product sp ON sp.id = si.sh_product_id
            WHERE s.user_id = :adminId
              AND (:clientId IS NULL OR si.client_id = :clientId)
            ORDER BY COALESCE(si.date, s.created_at) DESC, si.id DESC
            """, nativeQuery = true)
    List<ShSaleHistoryProjection> findAdminHistory(
            @Param("adminId") Long adminId,
            @Param("clientId") Long clientId);
}
