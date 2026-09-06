package com.apiproject.services.admin;

import com.apiproject.DTOs.Admin.CuponAssignmentRequestDTO;
import com.apiproject.DTOs.Admin.ServiceCuponRequestDTO;
import com.apiproject.DTOs.Admin.ServiceCuponResponseDTO;
import com.apiproject.DTOs.Admin.ServiceCuponToClientResponseDTO;
import com.apiproject.DTOs.General.CuponValidationResponseDTO;
import com.apiproject.entities.admin.ServiceCupon;
import com.apiproject.entities.admin.ServiceCuponToAClient;
import com.apiproject.entities.admin.ServiceOffered;
import com.apiproject.entities.client.UserClient;
import com.apiproject.exceptions.ResourceNotFoundException;
import com.apiproject.repositories.admin.ServiceCuponRepository;
import com.apiproject.repositories.admin.ServiceCuponToAClientRepository;
import com.apiproject.repositories.admin.ServiceOfferedRepository;
import com.apiproject.repositories.admin.UserRepository;
import com.apiproject.repositories.client.ClientRepository;
import com.apiproject.repositories.projection.ServiceCuponAssignmentProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
public class ServiceCuponService {
    private final ServiceCuponRepository serviceCuponRepository;
    private final ServiceCuponToAClientRepository serviceCuponToAClientRepository;
    private final ServiceOfferedRepository serviceOfferedRepository;
    private final UserRepository userRepository;
    private final ClientRepository clientRepository;

    // ================= CRUD (ADMIN) =================

    @Transactional
    public ServiceCuponResponseDTO create(ServiceCuponRequestDTO request, Long adminId) {
        validateRequest(request);
        ServiceCupon cupon = new ServiceCupon();
        applyFields(cupon, request);
        cupon.setUserAdmin(userRepository.getReferenceById(adminId));
        return ServiceCuponResponseDTO.fromEntity(serviceCuponRepository.save(cupon), adminId);
    }

    @Transactional(readOnly = true)
    public List<ServiceCuponResponseDTO> findAllByOwner(Long adminId) {
        return serviceCuponRepository.findAllByOwner(adminId).stream()
                .map(ServiceCuponResponseDTO::fromProjection)
                .toList();
    }

    @Transactional
    public ServiceCuponResponseDTO update(Long id, ServiceCuponRequestDTO request, Long adminId) {
        validateRequest(request);

        ServiceCupon cupon = serviceCuponRepository.lockById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service cupon not found: " + id));
        requireOwner(cupon, adminId);
        applyFields(cupon, request);

        return ServiceCuponResponseDTO.fromEntity(cupon, adminId);
    }

    @Transactional
    public void delete(Long id, Long adminId) {
        ServiceCupon cupon = serviceCuponRepository.lockById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service cupon not found: " + id));
        requireOwner(cupon, adminId);
        serviceCuponRepository.delete(cupon);
    }

    /** Valida un cupon de servicio contra los servicios de un admin (para el front antes de contratar). */
    @Transactional(readOnly = true)
    public CuponValidationResponseDTO validateForOwner(String code, Long ownerId) {
        return serviceCuponRepository.findValidForOwner(code, ownerId, LocalDateTime.now())
                .map(usage -> new CuponValidationResponseDTO(
                        true,
                        "Cupon valido",
                        BigDecimal.valueOf(usage.getDiscount()),
                        usage.getCuponId()))
                .orElseGet(() -> CuponValidationResponseDTO.invalid(
                        "El cupon no existe, esta expirado o agoto sus usos"));
    }

    // ================= Helpers =================

    private void applyFields(ServiceCupon cupon, ServiceCuponRequestDTO request) {
        cupon.setServiceCuponCode(request.serviceCuponCode().trim());
        cupon.setCuponDateLimit(request.cuponDateLimit());
        cupon.setDiscount(request.discount());
        cupon.setQuantity(request.quantity());
    }

    private void validateRequest(ServiceCuponRequestDTO request) {
        if (request == null || request.serviceCuponCode() == null || request.serviceCuponCode().isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST, "serviceCuponCode es obligatorio");
        }
        if (request.serviceCuponCode().length() > 15) {
            throw new ResponseStatusException(BAD_REQUEST, "serviceCuponCode no puede superar 15 caracteres");
        }
        if (request.cuponDateLimit() == null || request.cuponDateLimit().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(BAD_REQUEST, "cuponDateLimit debe ser una fecha futura");
        }
        if (request.discount() == null || request.discount() <= 0 || request.discount() > 100) {
            throw new ResponseStatusException(BAD_REQUEST, "discount debe estar entre 0 y 100 (porcentaje)");
        }
        if (request.quantity() != null && request.quantity() <= 0) {
            throw new ResponseStatusException(BAD_REQUEST, "quantity debe ser mayor a cero");
        }
    }

    private void requireOwner(ServiceCupon cupon, Long adminId) {
        Long ownerId = cupon.getUserAdmin() != null ? cupon.getUserAdmin().getId() : null;
        if (ownerId == null || !ownerId.equals(adminId)) {
            throw new ResponseStatusException(FORBIDDEN, "No puedes modificar cupones de otro admin");
        }
    }

    // ================= Asignación de cupones de servicio a clientes =================

    @Transactional
    public List<ServiceCuponToClientResponseDTO> assignToClients(CuponAssignmentRequestDTO request, Long adminId) {
        if (request.cuponId() == null) {
            throw new ResponseStatusException(BAD_REQUEST, "cuponId es obligatorio");
        }
        ServiceCupon cupon = serviceCuponRepository.lockById(request.cuponId())
                .orElseThrow(() -> new ResourceNotFoundException("Service cupon not found: " + request.cuponId()));
        requireOwner(cupon, adminId);

        List<ServiceOffered> services = serviceOfferedRepository.findAll().stream()
                .filter(s -> s.getUserAdmin() != null && s.getUserAdmin().getId().equals(adminId))
                .toList();
        if (services.isEmpty()) {
            throw new ResponseStatusException(CONFLICT, "El admin no tiene servicios ofrecidos");
        }

        List<Long> targetClientIds;
        if (request.assignToAll()) {
            targetClientIds = clientRepository.findAll().stream()
                    .map(UserClient::getId)
                    .toList();
        } else if (request.clientIds() != null && !request.clientIds().isEmpty()) {
            targetClientIds = request.clientIds();
        } else {
            throw new ResponseStatusException(BAD_REQUEST, "Debe indicar clientIds o assignToAll=true");
        }

        List<ServiceCuponToAClient> assignments = new ArrayList<>();
        for (Long clientId : targetClientIds) {
            UserClient client = clientRepository.getReferenceById(clientId);
            for (ServiceOffered service : services) {
                ServiceCuponToAClient assignment = new ServiceCuponToAClient();
                assignment.setClient(client);
                assignment.setServiceCupon(cupon);
                assignment.setService(service);
                assignments.add(assignment);
            }
        }
        serviceCuponToAClientRepository.saveAll(assignments);
        return findAllAssignmentsByAdmin(adminId);
    }

    @Transactional(readOnly = true)
    public List<ServiceCuponToClientResponseDTO> findAllAssignmentsByAdmin(Long adminId) {
        return serviceCuponToAClientRepository.findAllByAdmin(adminId).stream()
                .map(this::toAssignmentDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ServiceCuponToClientResponseDTO> findAssignmentsByClient(Long adminId, Long clientId) {
        return serviceCuponToAClientRepository.findByAdminAndClient(adminId, clientId).stream()
                .map(this::toAssignmentDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ServiceCuponToClientResponseDTO> findAssignmentsByCupon(Long adminId, Long cuponId) {
        return serviceCuponToAClientRepository.findByAdminAndCupon(adminId, cuponId).stream()
                .map(this::toAssignmentDto)
                .toList();
    }

    @Transactional
    public void removeAssignment(Long assignmentId, Long adminId) {
        ServiceCuponToAClient assignment = serviceCuponToAClientRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found: " + assignmentId));
        requireOwner(assignment.getServiceCupon(), adminId);
        serviceCuponToAClientRepository.deleteById(assignmentId);
    }

    @Transactional
    public void removeAllByCupon(Long cuponId, Long adminId) {
        ServiceCupon cupon = serviceCuponRepository.lockById(cuponId)
                .orElseThrow(() -> new ResourceNotFoundException("Service cupon not found: " + cuponId));
        requireOwner(cupon, adminId);
        serviceCuponToAClientRepository.deleteByCuponId(cuponId);
    }

    private ServiceCuponToClientResponseDTO toAssignmentDto(ServiceCuponAssignmentProjection p) {
        return new ServiceCuponToClientResponseDTO(
                p.getId(),
                p.getClientId(),
                p.getClientName(),
                p.getClientEmail(),
                p.getServiceCuponId(),
                p.getServiceCuponCode(),
                p.getDiscount(),
                p.getCuponDateLimit(),
                p.getServiceId(),
                p.getServiceName()
        );
    }
}
