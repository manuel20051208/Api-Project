package com.example.apiproject.services.user.client;

import com.example.apiproject.DTOs.ClientResponseDTO;
import com.example.apiproject.repositories.client.user.client.repository.ClientRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientService {
    private final ClientRepository clientRepository;

    public ClientService(ClientRepository client){
        this.clientRepository = client;
    }

    public List<ClientResponseDTO> searchForClientsFullName(String name){
        return clientRepository.findAllByFullNameContainingIgnoreCase(name).stream()
                .map(ClientResponseDTO::fromEntity)
                .toList();
    }

    public List<ClientResponseDTO> searchForClientsEmail(String email){
        return clientRepository.findAllByEmailContainingIgnoreCase(email).stream()
                .map(ClientResponseDTO::fromEntity)
                .toList();
    }

    public List<ClientResponseDTO> searForClientId(Long id){
        return clientRepository.findAllById(id).stream()
                .map(ClientResponseDTO::fromEntity)
                .toList();
    }
}