package com.apiproject.repositories.admin;

import com.apiproject.DTOs.General.TheThreeBestClients;
import com.apiproject.DTOs.General.TheThreeBestProducts;
import com.apiproject.entities.admin.UserAdmin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RankingsRepository extends JpaRepository<UserAdmin, Long> {
    @Query(value =
            """
            SELECT * FROM three_best_clients
            WHERE user_id = :userId 
            ORDER BY amount_of_buys DESC LIMIT 3
            """,nativeQuery = true)
    List<TheThreeBestClients> findAllByUser(Long userId);

    @Query(value =
            """
            SELECT * FROM three_best_products
            WHERE users_id = :userId 
            ORDER BY amount_of_buys DESC LIMIT 3
            """,
            nativeQuery = true)
    List<TheThreeBestProducts> findAllByUserId(Long userId);
}