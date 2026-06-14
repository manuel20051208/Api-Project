package com.example.apiproject.repositories.general;

import com.example.apiproject.entities.admin.UserAdmin;
import com.example.apiproject.entities.general.Product;
import io.micrometer.common.lang.NonNull;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByNameIgnoreCase(String name);

    List<Product> findAllByCategoryIgnoreCase(String category);

    @Query(
            value = "SELECT p.id FROM Product p WHERE p.active = true",
            countQuery = "SELECT COUNT(p) FROM Product p WHERE p.active = true"
    )
    Page<Long> findActiveProductIds(Pageable pageable);

    Optional<Product> findById(Long id);

    boolean existsByIdAndUserAdmin_Id(Long id, Long userAdminId);

    List<Product> findAllByUserAdmin_Id(Long userAdminId);

    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.images WHERE p.userAdmin.id = :userAdminId")
    List<Product> findAllByUserAdminIdWithImages(@Param("userAdminId") Long userAdminId);

    @Query("SELECT DISTINCT p FROM Product p JOIN FETCH p.images WHERE p.userAdmin.id = :userAdminId")
    List<Product> findAllWithImagesByUserAdminId(@Param("userAdminId") Long userAdminId);

    @Query("SELECT DISTINCT p FROM Product p JOIN FETCH p.images WHERE p.active = true ORDER BY p.name ASC")
    List<Product> findAllActiveWithImages();

    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.images WHERE LOWER(p.category) = LOWER(:category)")
    List<Product> findAllByCategoryIgnoreCaseWithImages(@Param("category") String category);

    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.images WHERE LOWER(p.name) = LOWER(:name)")
    Optional<Product> findByNameIgnoreCaseWithImages(@Param("name") String name);

    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.images WHERE p.id = :id")
    Optional<Product> findByIdWithImages(@Param("id") Long id);

    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.images WHERE p.id IN :ids")
    List<Product> findAllWithImagesByIdIn(@Param("ids") List<Long> ids);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id IN :ids")
    List<Product> findAllByIdInForUpdate(@Param("ids") List<Long> ids);

    @Query(value = "SELECT * FROM users u LEFT JOIN products p ON u.id = p.id_users " +
            "WHERE u.id = :userId LIMIT 1", nativeQuery = true)
    Optional<UserAdmin> findUserAdminByProductId(@Param("userId") Long userId);

    Optional<Product> getProductById(Long productId);

    @Transactional
    @Modifying
    void deleteById(@NonNull Long id);
}