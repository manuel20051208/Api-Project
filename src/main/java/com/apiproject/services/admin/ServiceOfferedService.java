package com.apiproject.services.admin;

import com.apiproject.DTOs.Admin.ServiceRequestDTO;
import com.apiproject.DTOs.Admin.ServiceResponseDTO;
import com.apiproject.entities.admin.ServiceOffered;
import com.apiproject.exceptions.ResourceNotFoundException;
import com.apiproject.repositories.admin.ServiceOfferedRepository;
import com.apiproject.repositories.admin.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;

@Service
@RequiredArgsConstructor
public class ServiceOfferedService {
    private final ServiceOfferedRepository serviceOfferedRepository;
    private final UserRepository userRepository;

    @Transactional
    public ServiceResponseDTO create(ServiceRequestDTO request, Long adminId) {
        validateRequest(request);
        ServiceOffered service = new ServiceOffered();
        applyFields(service, request);
        service.setUserAdmin(userRepository.getReferenceById(adminId));
        return ServiceResponseDTO.fromEntity(serviceOfferedRepository.save(service));
    }

    @Transactional(readOnly = true)
    public List<ServiceResponseDTO> findAllByOwner(Long adminId) {
        return serviceOfferedRepository.findAllByOwner(adminId).stream()
                .map(ServiceResponseDTO::fromEntity)
                .toList();
    }

    /** Catalogo publico de servicios de una tienda (por id del admin). */
    @Transactional(readOnly = true)
    public List<ServiceResponseDTO> findCatalog(Long ownerId, String search) {
        return serviceOfferedRepository.findCatalogByOwner(ownerId, normalizeSearch(search)).stream()
                .map(ServiceResponseDTO::fromEntity)
                .toList();
    }

    @Transactional
    public ServiceResponseDTO update(Long id, ServiceRequestDTO request, Long adminId) {
        validateRequest(request);
        requireOwner(id, adminId);

        ServiceOffered service = serviceOfferedRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found: " + id));
        applyFields(service, request);
        return ServiceResponseDTO.fromEntity(serviceOfferedRepository.save(service));
    }

    @Transactional
    public void delete(Long id, Long adminId) {
        requireOwner(id, adminId);
        serviceOfferedRepository.deleteById(id);
    }

    // ================= Helpers =================

    private void applyFields(ServiceOffered service, ServiceRequestDTO request) {
        service.setNameOfService(request.nameOfService());
        service.setValueOfService(request.valueOfService());
        service.setDescriptionOfService(request.descriptionOfService());
    }

    private void validateRequest(ServiceRequestDTO request) {
        if (request == null
                || request.nameOfService() == null || request.nameOfService().isBlank()
                || request.valueOfService() == null || request.valueOfService() < 0
                || request.descriptionOfService() == null || request.descriptionOfService().isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST,
                    "nameOfService, valueOfService y descriptionOfService son obligatorios");
        }
        if (request.nameOfService().length() > 40 || request.descriptionOfService().length() > 255) {
            throw new ResponseStatusException(BAD_REQUEST, "Longitud excedida en nameOfService o descriptionOfService");
        }
    }

    private void requireOwner(Long id, Long adminId) {
        if (!serviceOfferedRepository.existsByIdAndOwner(id, adminId)) {
            throw new ResponseStatusException(FORBIDDEN, "No puedes modificar servicios de otro admin");
        }
    }

    static String normalizeSearch(String search) {
        if (search == null || search.isBlank()) return null;
        return search.trim();
    }
}
