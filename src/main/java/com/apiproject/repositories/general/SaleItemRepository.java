package com.apiproject.repositories.general;

import com.apiproject.entities.general.SalesItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleItemRepository extends JpaRepository<SalesItem, Long> {

    /**
     * Valida que el cliente haya comprado el producto (requisito para poder reseñarlo).
     * Ignora ítems cancelados y acepta filas viejas sin estado.
     */
    @Query(value = """
            SELECT COUNT(*) > 0
            FROM sale_items
            WHERE client_id = :clientId
              AND product_id = :productId
              AND (state IS NULL OR state = 'COMPLETED')
            """, nativeQuery = true)
    boolean existsPurchaseByClientAndProduct(
            @Param("clientId") Long clientId,
            @Param("productId") Long productId);
}
