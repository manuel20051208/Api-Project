package com.example.apiproject.repositories.client;

import com.example.apiproject.entities.client.UserClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<UserClient, Long> {
    List<UserClient> findAllByFullNameContainingIgnoreCase(String fullName);
    List<UserClient> findAllByEmailContainingIgnoreCase(String email);
    List<UserClient> findAllById(Long id);
    Optional<UserClient> findByFullName(String fullName);
}