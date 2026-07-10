package com.example.apiproject.repositories.admin;

import com.example.apiproject.entities.general.Sale;
import com.example.apiproject.repositories.projection.SaleItemViewProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.Repository;

import java.util.List;

// quitamos el extends de JPA para aligerar la api y no cargar metodos del ORM (save(), delete() e.t.c...)
@org.springframework.stereotype.Repository
public interface SaleItemViewRepository extends Repository<Sale, Long> {
    @Query(value = "SELECT * FROM view_of_sales" +
            " WHERE client_name = :clientName AND user_id = :userId", nativeQuery = true)
    List<SaleItemViewProjection> findAllByClientName(@Param("userId") Long userId,
                                                     @Param("clientName") String clientName);

    @Query(value = "SELECT * FROM view_of_sales WHERE" +
            " product_name = :productName AND user_id = :userId", nativeQuery = true)
    List<SaleItemViewProjection> findALlByProductName(@Param("userId") Long userId,
                                                      @Param("productName") String productName);

    @Query(value = "SELECT * FROM view_of_sales WHERE user_id = :userId",
            countQuery = "SELECT count(*) FROM view_of_sales WHERE user_id = :userId",
            nativeQuery = true)
    Page<SaleItemViewProjection> findAll(@Param("userId") Long userId, Pageable pageable);
}