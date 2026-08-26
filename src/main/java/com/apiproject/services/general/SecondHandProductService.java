package com.apiproject.services.general;

import com.apiproject.DTOs.General.ShProductCardResponseDTO;
import com.apiproject.DTOs.General.ShProductResponseDTO;
import com.apiproject.entities.admin.UserAdmin;
import com.apiproject.entities.general.SecondHandProduct;
import com.apiproject.exceptions.ResourceNotFoundException;
import com.apiproject.repositories.admin.UserRepository;
import com.apiproject.repositories.general.SecondHandProductRepository;
import io.micrometer.common.lang.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.FORBIDDEN;

@Service
@RequiredArgsConstructor
public class SecondHandProductService {
    private final SecondHandProductRepository secondHandProductRepository;
    private final UserRepository userRepository;

    /** Catalogo publico de productos de segunda mano activos (una fila por producto, con primera imagen). */
    @Transactional(readOnly = true)
    public List<ShProductCardResponseDTO> findActiveCatalog(String category, String search) {
        return secondHandProductRepository.findActiveCatalogCards(
                        normalize(category), normalize(search)).stream()
                .map(ShProductCardResponseDTO::fromProjection)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ShProductCardResponseDTO> findAllByOwner(Long adminId, String search) {
        return secondHandProductRepository.findAdminCards(adminId, normalize(search)).stream()
                .map(ShProductCardResponseDTO::fromProjection)
                .toList();
    }

    @Transactional(readOnly = true)
    public ShProductResponseDTO findByIdOwned(Long id, Long adminId) {
        SecondHandProduct product = secondHandProductRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Second hand product not found: " + id));
        requireOwner(product, adminId);
        return ShProductResponseDTO.fromEntity(product);
    }

    @Transactional
    public void save(@NonNull SecondHandProduct product, Long adminId) {
        UserAdmin userAdmin = userRepository.getReferenceById(adminId);
        product.setUserAdmin(userAdmin);
        product.setActive(true);
        secondHandProductRepository.save(product);
    }

    @Transactional
    public ShProductResponseDTO updateProduct(Long id, @NonNull SecondHandProduct productRequest, Long adminId) {
        SecondHandProduct existing = secondHandProductRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Second hand product not found: " + id));
        requireOwner(existing, adminId);

        Optional.ofNullable(productRequest.getName()).ifPresent(existing::setName);
        Optional.ofNullable(productRequest.getPrice()).ifPresent(existing::setPrice);
        Optional.ofNullable(productRequest.getStock()).ifPresent(existing::setStock);
        Optional.ofNullable(productRequest.getCategory()).ifPresent(existing::setCategory);
        Optional.ofNullable(productRequest.getTimeOfUse()).ifPresent(existing::setTimeOfUse);
        Optional.ofNullable(productRequest.getLevelOfSecondHandProduct()).ifPresent(existing::setLevelOfSecondHandProduct);
        existing.setDescription(productRequest.getDescription());

        return ShProductResponseDTO.fromEntity(secondHandProductRepository.save(existing));
    }

    /** Borrado suave: deja el producto inactivo sin perder historial de ventas. */
    @Transactional
    public void deleteProductSafe(Long id, Long adminId) {
        SecondHandProduct product = secondHandProductRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Second hand product not found: " + id));
        requireOwner(product, adminId);
        product.setActive(false);
        secondHandProductRepository.save(product);
    }

    /** Referencia del producto validando que sea del admin (usado por el servicio de imagenes). */
    public SecondHandProduct findOwnedReference(Long productId, Long adminId) {
        if (!secondHandProductRepository.existsByIdAndOwner(productId, adminId)) {
            throw new ResponseStatusException(FORBIDDEN, "No puedes modificar productos de otro usuario admin");
        }
        return secondHandProductRepository.getReferenceById(productId);
    }

    // ================= Helpers =================

    private void requireOwner(SecondHandProduct product, Long adminId) {
        Long ownerId = product.getUserAdmin() != null ? product.getUserAdmin().getId() : null;
        if (ownerId == null || !ownerId.equals(adminId)) {
            throw new ResponseStatusException(FORBIDDEN, "No puedes modificar productos de otro usuario admin");
        }
    }

    static String normalize(String value) {
        if (value == null || value.isBlank()) return null;
        return value.trim();
    }
}
