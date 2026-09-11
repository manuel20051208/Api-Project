package com.apiproject.controllers.admin;

import com.apiproject.DTOs.Admin.CuponAssignmentRequestDTO;
import com.apiproject.DTOs.Admin.SecondHandCuponRequestDTO;
import com.apiproject.DTOs.Admin.SecondHandCuponResponseDTO;
import com.apiproject.DTOs.Admin.ShProductCuponToClientResponseDTO;
import com.apiproject.DTOs.General.CuponValidationResponseDTO;
import com.apiproject.security.AuthenticatedUser;
import com.apiproject.services.admin.SecondHandCuponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints de cupones para producto de segunda mano (/api/sh-cupons).
 * Espejo de {@link CuponController} contra {@link SecondHandCuponService}.
 */
@Tag(name = "Second Hand Cupon", description = "CRUD y validacion de cupones para productos de segunda mano")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sh-cupons")
public class SecondHandCuponController {
    private final SecondHandCuponService secondHandCuponService;

    @Operation(summary = "Crear un cupon de segunda mano (ADMIN)")
    @PostMapping
    public SecondHandCuponResponseDTO create(
            @RequestBody SecondHandCuponRequestDTO request,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        return secondHandCuponService.create(request, authenticatedUser.id());
    }

    @Operation(summary = "Listar los cupones de segunda mano del admin autenticado")
    @GetMapping("/my")
    public List<SecondHandCuponResponseDTO> findAllByOwner(@AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        return secondHandCuponService.findAllByOwner(authenticatedUser.id());
    }

    @Operation(summary = "Actualizar un cupon de segunda mano del admin autenticado")
    @PutMapping("/{id}")
    public SecondHandCuponResponseDTO update(
            @PathVariable Long id,
            @RequestBody SecondHandCuponRequestDTO request,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        return secondHandCuponService.update(id, request, authenticatedUser.id());
    }

    @Operation(summary = "Eliminar un cupon de segunda mano del admin autenticado")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        secondHandCuponService.delete(id, authenticatedUser.id());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Validar un cupon para un producto de segunda mano (CLIENT)")
    @GetMapping("/validate")
    public CuponValidationResponseDTO validate(
            @RequestParam String code,
            @RequestParam Long productId
    ) {
        return secondHandCuponService.validateForShProduct(code, productId);
    }

    // ================= Asignación de cupones SH a clientes =================

    @Operation(summary = "Asignar un cupon SH a clientes específicos o a todos (ADMIN)")
    @PostMapping("/assign")
    public List<ShProductCuponToClientResponseDTO> assign(
            @RequestBody CuponAssignmentRequestDTO request,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        return secondHandCuponService.assignToClients(request, authenticatedUser.id());
    }

    @Operation(summary = "Listar todas las asignaciones de cupones SH del admin")
    @GetMapping("/assignments")
    public List<ShProductCuponToClientResponseDTO> findAllAssignments(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        return secondHandCuponService.findAllAssignmentsByAdmin(authenticatedUser.id());
    }

    @Operation(summary = "Listar asignaciones de un cupón SH específico")
    @GetMapping("/assignments/cupon/{cuponId}")
    public List<ShProductCuponToClientResponseDTO> findAssignmentsByCupon(
            @PathVariable Long cuponId,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        return secondHandCuponService.findAssignmentsByCupon(authenticatedUser.id(), cuponId);
    }

    @Operation(summary = "Listar asignaciones de cupones SH para un cliente específico")
    @GetMapping("/assignments/client/{clientId}")
    public List<ShProductCuponToClientResponseDTO> findAssignmentsByClient(
            @PathVariable Long clientId,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        return secondHandCuponService.findAssignmentsByClient(authenticatedUser.id(), clientId);
    }

    @Operation(summary = "Eliminar una asignación SH específica")
    @DeleteMapping("/assignments/{assignmentId}")
    public ResponseEntity<Void> removeAssignment(
            @PathVariable Long assignmentId,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        secondHandCuponService.removeAssignment(assignmentId, authenticatedUser.id());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Eliminar todas las asignaciones de un cupón SH")
    @DeleteMapping("/assignments/cupon/{cuponId}")
    public ResponseEntity<Void> removeAllByCupon(
            @PathVariable Long cuponId,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        secondHandCuponService.removeAllByCupon(cuponId, authenticatedUser.id());
        return ResponseEntity.noContent().build();
    }
}
