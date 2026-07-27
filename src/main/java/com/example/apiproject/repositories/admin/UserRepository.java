package com.example.apiproject.repositories.admin;

import com.example.apiproject.entities.admin.UserAdmin;
import jakarta.transaction.Transactional;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserAdmin, Long> {
    Optional<UserAdmin> findById(Long id);
    boolean existsByEmail(String email);
    Optional<UserAdmin> findByEmail(String email);
    Optional<UserAdmin> findProfileById(@Param("id") Long id);

    UserAdmin save (@NonNull UserAdmin userAdmin);

    @Modifying
    @Transactional
    @Query("UPDATE UserAdmin u SET u.password = :password WHERE u.id = :id")
    void updatePasswordById(@Param("id") Long id, @Param("password") String password);
}
