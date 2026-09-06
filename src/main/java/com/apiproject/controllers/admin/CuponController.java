package com.apiproject.controllers.admin;

import com.apiproject.DTOs.Admin.CuponAssignmentRequestDTO;
import com.apiproject.DTOs.Admin.CuponRequestDTO;
import com.apiproject.DTOs.Admin.CuponResponseDTO;
import com.apiproject.DTOs.Admin.ProductCuponToClientResponseDTO;
import com.apiproject.DTOs.General.CuponValidationResponseDTO;
import com.apiproject.security.AuthenticatedUser;
import com.apiproject.services.admin.CuponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Cupon", description = "CRUD y validacion de cupones para productos normales")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cupons")
public class CuponController {
    private final CuponService cuponService;

    @Operation(summary = "Crear un cupon (ADMIN) con los productos a los que aplica")
    @PostMapping
    public CuponResponseDTO create(
            @RequestBody CuponRequestDTO request,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        return cuponService.create(request, authenticatedUser.id());
    }

    @Operation(summary = "Listar los cupones del admin autenticado")
    @GetMapping("/my")
    public List<CuponResponseDTO> findAllByOwner(@AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        return cuponService.findAllByOwner(authenticatedUser.id());
    }

    @Operation(summary = "Actualizar un cupon del admin autenticado")
    @PutMapping("/{id}")
    public CuponResponseDTO update(
            @PathVariable Long id,
            @RequestBody CuponRequestDTO request,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        return cuponService.update(id, request, authenticatedUser.id());
    }

    @Operation(summary = "Eliminar un cupon del admin autenticado")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        cuponService.delete(id, authenticatedUser.id());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Validar un cupon para un producto (CLIENT, antes de comprar)")
    @GetMapping("/validate")
    public CuponValidationResponseDTO validate(
            @RequestParam String code,
            @RequestParam Long productId
    ) {
        return cuponService.validateForProduct(code, productId);
    }

    // ================= Asignación de cupones a clientes =================

    @Operation(summary = "Asignar un cupon a clientes específicos o a todos (ADMIN)")
    @PostMapping("/assign")
    public List<ProductCuponToClientResponseDTO> assign(
            @RequestBody CuponAssignmentRequestDTO request,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        return cuponService.assignToClients(request, authenticatedUser.id());
    }

    @Operation(summary = "Listar todas las asignaciones de cupones del admin")
    @GetMapping("/assignments")
    public List<ProductCuponToClientResponseDTO> findAllAssignments(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        return cuponService.findAllAssignmentsByAdmin(authenticatedUser.id());
    }

    @Operation(summary = "Listar asignaciones de un cupón específico")
    @GetMapping("/assignments/cupon/{cuponId}")
    public List<ProductCuponToClientResponseDTO> findAssignmentsByCupon(
            @PathVariable Long cuponId,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        return cuponService.findAssignmentsByCupon(authenticatedUser.id(), cuponId);
    }

    @Operation(summary = "Listar asignaciones de cupones para un cliente específico")
    @GetMapping("/assignments/client/{clientId}")
    public List<ProductCuponToClientResponseDTO> findAssignmentsByClient(
            @PathVariable Long clientId,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        return cuponService.findAssignmentsByClient(authenticatedUser.id(), clientId);
    }

    @Operation(summary = "Listar cupones asignados al cliente autenticado (CLIENT)")
    @GetMapping("/assignments/my")
    public List<ProductCuponToClientResponseDTO> findMyAssignments(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        return cuponService.findMyAssignments(authenticatedUser.id());
    }

    @Operation(summary = "Eliminar una asignación específica")
    @DeleteMapping("/assignments/{assignmentId}")
    public ResponseEntity<Void> removeAssignment(
            @PathVariable Long assignmentId,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        cuponService.removeAssignment(assignmentId, authenticatedUser.id());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Eliminar todas las asignaciones de un cupón")
    @DeleteMapping("/assignments/cupon/{cuponId}")
    public ResponseEntity<Void> removeAllByCupon(
            @PathVariable Long cuponId,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        cuponService.removeAllByCupon(cuponId, authenticatedUser.id());
        return ResponseEntity.noContent().build();
    }
}
