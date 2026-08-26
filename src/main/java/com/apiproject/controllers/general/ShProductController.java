package com.apiproject.controllers.general;

import com.apiproject.DTOs.General.ShProductCardResponseDTO;
import com.apiproject.DTOs.General.ShProductResponseDTO;
import com.apiproject.entities.general.SecondHandProduct;
import com.apiproject.security.AuthenticatedUser;
import com.apiproject.services.general.SecondHandProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Second Hand Product", description = "CRUD y catalogo de productos de segunda mano")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sh-product")
public class ShProductController {
    private final SecondHandProductService secondHandProductService;

    @Operation(summary = "Catalogo activo de productos de segunda mano (filtros opcionales category/search)")
    @GetMapping("/search/active")
    public List<ShProductCardResponseDTO> getActiveCatalog(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search
    ) {
        return secondHandProductService.findActiveCatalog(category, search);
    }

    @Operation(summary = "Productos de segunda mano del admin autenticado (incluye inactivos)")
    @GetMapping("/my")
    public List<ShProductCardResponseDTO> findAllByOwner(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @RequestParam(required = false) String search
    ) {
        return secondHandProductService.findAllByOwner(authenticatedUser.id(), search);
    }

    @Operation(summary = "Detalle de un producto de segunda mano propio")
    @GetMapping("/{id}")
    public ShProductResponseDTO findById(
            @PathVariable Long id,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        return secondHandProductService.findByIdOwned(id, authenticatedUser.id());
    }

    @Operation(summary = "Guardar un producto de segunda mano (ADMIN)")
    @PostMapping(value = "/saveShProduct", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ShProductResponseDTO saveShProduct(
            @RequestBody SecondHandProduct product,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        secondHandProductService.save(product, authenticatedUser.id());
        return ShProductResponseDTO.fromEntity(product);
    }

    @Operation(summary = "Borrado suave de un producto de segunda mano (ADMIN)")
    @PostMapping("/deleteSafe")
    public ResponseEntity<ShProductResponseDTO> deleteSafe(
            SecondHandProduct product,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        secondHandProductService.deleteProductSafe(product.getId(), authenticatedUser.id());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Actualizar un producto de segunda mano (ADMIN)")
    @PutMapping("/update/{id}")
    public ShProductResponseDTO updateProduct(
            @PathVariable long id,
            @RequestBody SecondHandProduct productRequest,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        return secondHandProductService.updateProduct(id, productRequest, authenticatedUser.id());
    }
}
