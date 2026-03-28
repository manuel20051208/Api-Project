package com.example.apiproject.services.user.admin;

import com.example.apiproject.DTOs.UserResponseDTO;
import com.example.apiproject.repositories.admin.user.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponseDTO loginBizAdmin(String userName, String password) {
        return userRepository.findByUserName(userName)
                .filter(users -> users.getPassword().equals(password))
                .map(UserResponseDTO::fromEntity)
                .orElseThrow(() -> new RuntimeException("UserAdmin or password are incorrect"));
    }
}