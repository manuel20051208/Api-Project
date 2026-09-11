package com.apiproject.controllers.admin;

import com.apiproject.DTOs.Admin.CuponAssignmentRequestDTO;
import com.apiproject.DTOs.Admin.ServiceCuponRequestDTO;
import com.apiproject.DTOs.Admin.ServiceCuponResponseDTO;
import com.apiproject.DTOs.Admin.ServiceCuponToClientResponseDTO;
import com.apiproject.DTOs.General.CuponValidationResponseDTO;
import com.apiproject.security.AuthenticatedUser;
import com.apiproject.services.admin.ServiceCuponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints de cupones para servicios ofrecidos (/api/services-cupons).
 * CRUD y asignación a clientes delegan en {@link ServiceCuponService}.
 */
@Tag(name = "Services Cupon", description = "CRUD y validacion de cupones para servicios")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/services-cupons")
public class ServiceCuponController {
    private final ServiceCuponService serviceCuponService;

    @Operation(summary = "Crear un cupon de servicios (ADMIN)")
    @PostMapping
    public ServiceCuponResponseDTO create(
            @RequestBody ServiceCuponRequestDTO request,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        return serviceCuponService.create(request, authenticatedUser.id());
    }

    @Operation(summary = "Listar los cupones de servicios del admin autenticado")
    @GetMapping("/my")
    public List<ServiceCuponResponseDTO> findAllByOwner(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        return serviceCuponService.findAllByOwner(authenticatedUser.id());
    }

    @Operation(summary = "Actualizar un cupon de servicios del admin autenticado")
    @PutMapping("/{id}")
    public ServiceCuponResponseDTO update(
            @PathVariable Long id,
            @RequestBody ServiceCuponRequestDTO request,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        return serviceCuponService.update(id, request, authenticatedUser.id());
    }

    @Operation(summary = "Eliminar un cupon de servicios del admin autenticado")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        serviceCuponService.delete(id, authenticatedUser.id());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Validar un cupon de servicio contra una tienda (publico/autenticado)")
    @GetMapping("/validate/{ownerId}")
    public CuponValidationResponseDTO validate(
            @PathVariable Long ownerId,
            @RequestParam String code
    ) {
        return serviceCuponService.validateForOwner(code, ownerId);
    }

    // ================= Asignación de cupones de servicio a clientes =================

    @Operation(summary = "Asignar un cupon de servicio a clientes específicos o a todos (ADMIN)")
    @PostMapping("/assign")
    public List<ServiceCuponToClientResponseDTO> assign(
            @RequestBody CuponAssignmentRequestDTO request,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        return serviceCuponService.assignToClients(request, authenticatedUser.id());
    }

    @Operation(summary = "Listar todas las asignaciones de cupones de servicio del admin")
    @GetMapping("/assignments")
    public List<ServiceCuponToClientResponseDTO> findAllAssignments(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        return serviceCuponService.findAllAssignmentsByAdmin(authenticatedUser.id());
    }

    @Operation(summary = "Listar asignaciones de un cupón de servicio específico")
    @GetMapping("/assignments/cupon/{cuponId}")
    public List<ServiceCuponToClientResponseDTO> findAssignmentsByCupon(
            @PathVariable Long cuponId,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        return serviceCuponService.findAssignmentsByCupon(authenticatedUser.id(), cuponId);
    }

    @Operation(summary = "Listar asignaciones de cupones de servicio para un cliente específico")
    @GetMapping("/assignments/client/{clientId}")
    public List<ServiceCuponToClientResponseDTO> findAssignmentsByClient(
            @PathVariable Long clientId,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        return serviceCuponService.findAssignmentsByClient(authenticatedUser.id(), clientId);
    }

    @Operation(summary = "Eliminar una asignación de servicio específica")
    @DeleteMapping("/assignments/{assignmentId}")
    public ResponseEntity<Void> removeAssignment(
            @PathVariable Long assignmentId,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        serviceCuponService.removeAssignment(assignmentId, authenticatedUser.id());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Eliminar todas las asignaciones de un cupón de servicio")
    @DeleteMapping("/assignments/cupon/{cuponId}")
    public ResponseEntity<Void> removeAllByCupon(
            @PathVariable Long cuponId,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        serviceCuponService.removeAllByCupon(cuponId, authenticatedUser.id());
        return ResponseEntity.noContent().build();
    }
}
