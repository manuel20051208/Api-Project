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

/**
 * Lógica de cupones de servicios ofrecidos por una tienda.
 * A diferencia de los cupones de producto, estos aplican al conjunto de servicios
 * del admin (sin vínculo por producto individual) y se asignan a clientes igual que el resto.
 */
@Service
@RequiredArgsConstructor
public class ServiceCuponService {
    private final ServiceCuponRepository serviceCuponRepository;
    private final ServiceCuponToAClientRepository serviceCuponToAClientRepository;
    private final ServiceOfferedRepository serviceOfferedRepository;
    private final UserRepository userRepository;
    private final ClientRepository clientRepository;

    // ================= CRUD (ADMIN) =================

    /** Crea un cupón de servicio para el admin autenticado. */
    @Transactional
    public ServiceCuponResponseDTO create(ServiceCuponRequestDTO request, Long adminId) {
        validateRequest(request);
        ServiceCupon cupon = new ServiceCupon();
        applyFields(cupon, request);
        cupon.setUserAdmin(userRepository.getReferenceById(adminId)); // dueño = admin autenticado
        return ServiceCuponResponseDTO.fromEntity(serviceCuponRepository.save(cupon), adminId);
    }

    /** Lista los cupones de servicio del admin. */
    @Transactional(readOnly = true)
    public List<ServiceCuponResponseDTO> findAllByOwner(Long adminId) {
        return serviceCuponRepository.findAllByOwner(adminId).stream()
                .map(ServiceCuponResponseDTO::fromProjection)
                .toList();
    }

    /** Actualiza un cupón de servicio propio (LOCK FOR UPDATE). */
    @Transactional
    public ServiceCuponResponseDTO update(Long id, ServiceCuponRequestDTO request, Long adminId) {
        validateRequest(request);

        ServiceCupon cupon = serviceCuponRepository.lockById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service cupon not found: " + id));
        requireOwner(cupon, adminId); // solo el dueño lo edita
        applyFields(cupon, request);

        return ServiceCuponResponseDTO.fromEntity(cupon, adminId);
    }

    /** Borra un cupón de servicio propio (validando dueño). No tiene N:M que limpiar. */
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

    /** Copia los campos editables del request a la entidad. */
    private void applyFields(ServiceCupon cupon, ServiceCuponRequestDTO request) {
        cupon.setServiceCuponCode(request.serviceCuponCode().trim());
        cupon.setCuponDateLimit(request.cuponDateLimit());
        cupon.setDiscount(request.discount());
        cupon.setQuantity(request.quantity());
    }

    /** Valida los campos del request: código no vacío (<=15), fecha futura, descuento 1-100, usos > 0. */
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

    /** Verifica que el cupón de servicio pertenezca al admin. */
    private void requireOwner(ServiceCupon cupon, Long adminId) {
        Long ownerId = cupon.getUserAdmin() != null ? cupon.getUserAdmin().getId() : null;
        if (ownerId == null || !ownerId.equals(adminId)) {
            throw new ResponseStatusException(FORBIDDEN, "No puedes modificar cupones de otro admin");
        }
    }

    // ================= Asignación de cupones de servicio a clientes =================

    /**
     * Asigna un cupón de servicio a clientes (todos o una lista).
     * Aquí el cupón se cruza con los servicios del admin: cada cliente recibe una
     * fila por cada servicio ofrecido por esa tienda.
     */
    @Transactional
    public List<ServiceCuponToClientResponseDTO> assignToClients(CuponAssignmentRequestDTO request, Long adminId) {
        if (request.cuponId() == null) {
            throw new ResponseStatusException(BAD_REQUEST, "cuponId es obligatorio");
        }
        ServiceCupon cupon = serviceCuponRepository.lockById(request.cuponId()) // LOCK contra duplicados
                .orElseThrow(() -> new ResourceNotFoundException("Service cupon not found: " + request.cuponId()));
        requireOwner(cupon, adminId);

        // Los servicios objetivo son los del propio admin (no hay vínculo por producto)
        List<ServiceOffered> services = serviceOfferedRepository.findAll().stream()
                .filter(s -> s.getUserAdmin() != null && s.getUserAdmin().getId().equals(adminId))
                .toList();
        if (services.isEmpty()) {
            throw new ResponseStatusException(CONFLICT, "El admin no tiene servicios ofrecidos");
        }

        // Destinatarios: todos los clientes o la lista indicada
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

        // Producto cruzado: cada cliente recibe una fila por cada servicio de la tienda
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
        return findAllAssignmentsByAdmin(adminId); // estado completo tras asignar
    }

    /** Todas las asignaciones de cupones de servicio del admin. */
    @Transactional(readOnly = true)
    public List<ServiceCuponToClientResponseDTO> findAllAssignmentsByAdmin(Long adminId) {
        return serviceCuponToAClientRepository.findAllByAdmin(adminId).stream()
                .map(this::toAssignmentDto)
                .toList();
    }

    /** Asignaciones del admin hacia un cliente concreto. */
    @Transactional(readOnly = true)
    public List<ServiceCuponToClientResponseDTO> findAssignmentsByClient(Long adminId, Long clientId) {
        return serviceCuponToAClientRepository.findByAdminAndClient(adminId, clientId).stream()
                .map(this::toAssignmentDto)
                .toList();
    }

    /** Asignaciones de un cupón de servicio concreto del admin. */
    @Transactional(readOnly = true)
    public List<ServiceCuponToClientResponseDTO> findAssignmentsByCupon(Long adminId, Long cuponId) {
        return serviceCuponToAClientRepository.findByAdminAndCupon(adminId, cuponId).stream()
                .map(this::toAssignmentDto)
                .toList();
    }

    /** Elimina una asignación puntual (validando dueño del cupón). */
    @Transactional
    public void removeAssignment(Long assignmentId, Long adminId) {
        ServiceCuponToAClient assignment = serviceCuponToAClientRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found: " + assignmentId));
        requireOwner(assignment.getServiceCupon(), adminId);
        serviceCuponToAClientRepository.deleteById(assignmentId);
    }

    /** Elimina todas las asignaciones de un cupón de servicio (validando dueño). */
    @Transactional
    public void removeAllByCupon(Long cuponId, Long adminId) {
        ServiceCupon cupon = serviceCuponRepository.lockById(cuponId)
                .orElseThrow(() -> new ResourceNotFoundException("Service cupon not found: " + cuponId));
        requireOwner(cupon, adminId);
        serviceCuponToAClientRepository.deleteByCuponId(cuponId);
    }

    /** Proyección -> DTO de una asignación de servicio (sin casteo manual). */
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