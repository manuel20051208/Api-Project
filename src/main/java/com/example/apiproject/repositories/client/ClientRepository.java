package com.example.apiproject.repositories.client;

import com.example.apiproject.entities.client.UserClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<UserClient, Long> {
    Optional<UserClient> findById(Long id);

    boolean existsByUserName(String userName);

    boolean existsByEmail(String email);

    @Query(value = "SELECT * FROM clients WHERE username = :userName", nativeQuery = true)
    Optional<UserClient> findLoginByUserName(@Param("userName") String userName);

    @Query(value = "SELECT * FROM clients WHERE LOWER(full_name) LIKE LOWER(CONCAT('%', :fullName, '%'))", nativeQuery = true)
    List<UserClient> findSummaryByFullNameContainingIgnoreCase(@Param("fullName") String fullName);

    @Query(value = "SELECT * FROM clients WHERE LOWER(email) LIKE LOWER(CONCAT('%', :email, '%'))", nativeQuery = true)
    List<UserClient> findSummaryByEmailContainingIgnoreCase(@Param("email") String email);

    @Query(value = "SELECT * FROM clients WHERE id = :id", nativeQuery = true)
    Optional<UserClient> findSummaryById(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update UserClient c set c.password = :password where c.id = :id")
    void updatePasswordById(@Param("id") Long id, @Param("password") String password);
}
