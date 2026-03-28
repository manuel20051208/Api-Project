package com.example.apiproject.repositories.admin.user.repositories;

import com.example.apiproject.entities.admin.UserAdmin;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserAdmin, Long> {
    UserAdmin findUserByGoogleId(Long googleId);

    Optional<UserAdmin> findByUserName(String userName);

    @Transactional
    @Modifying
    void deleteAllByGoogleId(Long googleId);
}