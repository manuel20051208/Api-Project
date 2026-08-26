package com.apiproject.repositories.admin;

import com.apiproject.entities.admin.ServiceOffered;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceOfferedRepository extends JpaRepository<ServiceOffered, Long> {

    @Query(value = "SELECT * FROM services_offered WHERE user_id = :adminId ORDER BY id", nativeQuery = true)
    List<ServiceOffered> findAllByOwner(@Param("adminId") Long adminId);

    @Query(value = """
            SELECT * FROM services_offered
            WHERE user_id = :adminId
              AND (:search IS NULL OR LOWER(name_of_service) LIKE '%' || LOWER(:search) || '%')
            ORDER BY id
            """, nativeQuery = true)
    List<ServiceOffered> findCatalogByOwner(@Param("adminId") Long adminId, @Param("search") String search);

    @Query(value = "SELECT COUNT(*) > 0 FROM services_offered WHERE id = :id AND user_id = :adminId", nativeQuery = true)
    boolean existsByIdAndOwner(@Param("id") Long id, @Param("adminId") Long adminId);
}
