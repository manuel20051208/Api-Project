package com.apiproject.controllers.admin;

import com.apiproject.DTOs.Admin.ServiceRequestDTO;
import com.apiproject.DTOs.Admin.ServiceResponseDTO;
import com.apiproject.security.AuthenticatedUser;
import com.apiproject.services.admin.ServiceOfferedService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Services Offered", description = "CRUD de servicios ofrecidos por la tienda")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/services")
public class ServiceOfferedController {
    private final ServiceOfferedService serviceOfferedService;

    @Operation(summary = "Crear un servicio (ADMIN)")
    @PostMapping
    public ServiceResponseDTO create(
            @RequestBody ServiceRequestDTO request,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        return serviceOfferedService.create(request, authenticatedUser.id());
    }

    @Operation(summary = "Listar los servicios del admin autenticado")
    @GetMapping("/my")
    public List<ServiceResponseDTO> findAllByOwner(@AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        return serviceOfferedService.findAllByOwner(authenticatedUser.id());
    }

    @Operation(summary = "Catalogo publico de servicios de una tienda por id del admin")
    @GetMapping("/catalog/{ownerId}")
    public List<ServiceResponseDTO> findCatalog(
            @PathVariable Long ownerId,
            @RequestParam(required = false) String search
    ) {
        return serviceOfferedService.findCatalog(ownerId, search);
    }

    @Operation(summary = "Actualizar un servicio del admin autenticado")
    @PutMapping("/{id}")
    public ServiceResponseDTO update(
            @PathVariable Long id,
            @RequestBody ServiceRequestDTO request,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        return serviceOfferedService.update(id, request, authenticatedUser.id());
    }

    @Operation(summary = "Eliminar un servicio del admin autenticado")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        serviceOfferedService.delete(id, authenticatedUser.id());
        return ResponseEntity.noContent().build();
    }
}
