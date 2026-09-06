package com.apiproject.services.admin;

import com.apiproject.DTOs.Admin.CuponAssignmentRequestDTO;
import com.apiproject.DTOs.Admin.CuponRequestDTO;
import com.apiproject.DTOs.Admin.CuponResponseDTO;
import com.apiproject.DTOs.Admin.SecondHandCuponRequestDTO;
import com.apiproject.DTOs.Admin.SecondHandCuponResponseDTO;
import com.apiproject.DTOs.Admin.ShProductCuponToClientResponseDTO;
import com.apiproject.DTOs.General.CuponValidationResponseDTO;
import com.apiproject.entities.admin.SecondHandCupon;
import com.apiproject.entities.admin.SecondHandProductCuponsApplied;
import com.apiproject.entities.admin.ShProductCuponToAClient;
import com.apiproject.entities.client.UserClient;
import com.apiproject.entities.general.SecondHandProduct;
import com.apiproject.exceptions.ResourceNotFoundException;
import com.apiproject.repositories.admin.SecondHandCuponRepository;
import com.apiproject.repositories.admin.SecondHandProductCuponRepository;
import com.apiproject.repositories.admin.ShCuponUsedByClientsRepository;
import com.apiproject.repositories.admin.ShProductCuponToAClientRepository;
import com.apiproject.repositories.admin.UserRepository;
import com.apiproject.repositories.client.ClientRepository;
import com.apiproject.repositories.general.SecondHandProductRepository;
import com.apiproject.repositories.projection.CuponAdminProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
public class SecondHandCuponService {
    private final SecondHandCuponRepository secondHandCuponRepository;
    private final SecondHandProductCuponRepository secondHandProductCuponRepository;
    private final ShCuponUsedByClientsRepository shCuponUsedByClientsRepository;
    private final ShProductCuponToAClientRepository shProductCuponToAClientRepository;
    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final SecondHandProductRepository secondHandProductRepository;

    // ================= CRUD (ADMIN) =================

    @Transactional
    public SecondHandCuponResponseDTO create(SecondHandCuponRequestDTO request, Long adminId) {
        validateRequest(request);
        validateProductsOwned(request.shProductIds(), adminId);

        SecondHandCupon cupon = new SecondHandCupon();
        applyFields(cupon, request);
        cupon.setUserAdmin(userRepository.getReferenceById(adminId));
        SecondHandCupon saved = secondHandCuponRepository.save(cupon);
        replaceProducts(saved, request.shProductIds());

        return new SecondHandCuponResponseDTO(
                saved.getId(),
                saved.getShCuponCode(),
                saved.getCuponDateLimit(),
                saved.getDiscount(),
                saved.getQuantity(),
                true,
                adminId,
                secondHandProductCuponRepository.findShProductIdsByCuponId(saved.getId()));
    }

    @Transactional(readOnly = true)
    public List<SecondHandCuponResponseDTO> findAllByOwner(Long adminId) {
        return secondHandCuponRepository.findAllByOwner(adminId).stream()
                .map(projection -> SecondHandCuponResponseDTO.fromProjection(
                        projection,
                        CuponService.parseIds(projection.getAppliedProductIds())))
                .toList();
    }

    @Transactional
    public SecondHandCuponResponseDTO update(Long id, SecondHandCuponRequestDTO request, Long adminId) {
        validateRequest(request);

        SecondHandCupon cupon = secondHandCuponRepository.lockById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Second hand cupon not found: " + id));
        requireOwner(cupon, adminId);

        applyFields(cupon, request);
        if (request.shProductIds() != null) {
            secondHandProductCuponRepository.deleteByCuponId(cupon.getId());
            replaceProducts(cupon, request.shProductIds());
        }

        return new SecondHandCuponResponseDTO(
                cupon.getId(),
                cupon.getShCuponCode(),
                cupon.getCuponDateLimit(),
                cupon.getDiscount(),
                cupon.getQuantity(),
                CuponResponseDTO.isUsable(cupon.getCuponDateLimit(), cupon.getQuantity()),
                adminId,
                secondHandProductCuponRepository.findShProductIdsByCuponId(cupon.getId()));
    }

    @Transactional
    public void delete(Long id, Long adminId) {
        SecondHandCupon cupon = secondHandCuponRepository.lockById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Second hand cupon not found: " + id));
        requireOwner(cupon, adminId);
        secondHandProductCuponRepository.deleteByCuponId(id);
        secondHandCuponRepository.delete(cupon);
    }

    // ============ Validación pública (CLIENT) ============

    @Transactional(readOnly = true)
    public CuponValidationResponseDTO validateForShProduct(String code, Long shProductId) {
        return secondHandCuponRepository.findValidForShProduct(code, shProductId, LocalDateTime.now())
                .map(usage -> new CuponValidationResponseDTO(
                        true,
                        "Cupon valido",
                        java.math.BigDecimal.valueOf(usage.getDiscount()),
                        usage.getCuponId()))
                .orElseGet(() -> CuponValidationResponseDTO.invalid(
                        "El cupon no existe, esta expirado, agoto sus usos o no aplica a este producto"));
    }

    /**
     * Igual que CuponService.resolveForCart pero para productos de segunda mano.
     * El cupon pertenece a un solo admin y aplica descuento solo a los productos del
     * carrito de ese admin que esten vinculados; se permite carrito multi-vendedor.
     */
    @Transactional
    public CouponResolution resolveForCart(String code, List<Long> shCarritoProductIds,
                                           java.util.function.Function<Long, Long> shProductoOwnerFn) {
        SecondHandCupon cupon = secondHandCuponRepository.lockByCode(code)
                .orElseThrow(() -> new ResponseStatusException(CONFLICT, "El cupon no existe: " + code));

        if (cupon.getCuponDateLimit().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(CONFLICT, "El cupon esta expirado");
        }
        if (cupon.getQuantity() != null && cupon.getQuantity() <= 0) {
            throw new ResponseStatusException(CONFLICT, "El cupon agoto sus usos");
        }

        Long cuponOwnerId = cupon.getUserAdmin().getId();
        Set<Long> linkedProducts = new LinkedHashSet<>(
                secondHandProductCuponRepository.findShProductIdsByCuponId(cupon.getId()));

        List<Long> elegibles = shCarritoProductIds.stream()
                .filter(id -> cuponOwnerId.equals(shProductoOwnerFn.apply(id)))
                .filter(linkedProducts::contains)
                .toList();

        if (elegibles.isEmpty()) {
            throw new ResponseStatusException(CONFLICT,
                    "El cupon no aplica a ningun producto del carrito de este vendedor");
        }

        Integer quantity = cupon.getQuantity();
        if (quantity != null && elegibles.size() > quantity) {
            elegibles = elegibles.subList(0, quantity);
        }

        return new CouponResolution(cupon.getId(), cupon.getDiscount(), cuponOwnerId, elegibles);
    }

    @Transactional
    public void redeem(CouponResolution resolution) {
        int updatedRows = secondHandCuponRepository.decrementQuantity(resolution.cuponId());
        if (updatedRows == 0) {
            throw new ResponseStatusException(CONFLICT, "El cupon agoto sus usos");
        }
    }

    public record CouponResolution(Long cuponId, Double discountPercent, Long ownerId, List<Long> elegibleProductIds) {
    }

    // ================= Helpers =================

    private void applyFields(SecondHandCupon cupon, SecondHandCuponRequestDTO request) {
        cupon.setShCuponCode(request.shCuponCode().trim());
        cupon.setCuponDateLimit(request.cuponDateLimit());
        cupon.setDiscount(request.discount());
        cupon.setQuantity(request.quantity());
    }

    private void replaceProducts(SecondHandCupon cupon, List<Long> shProductIds) {
        List<SecondHandProductCuponsApplied> links = new ArrayList<>();
        for (Long productId : distinct(shProductIds)) {
            SecondHandProductCuponsApplied link = new SecondHandProductCuponsApplied();
            link.setCupon(cupon);
            link.setProduct(secondHandProductRepository.getReferenceById(productId));
            links.add(link);
        }
        secondHandProductCuponRepository.saveAll(links);
    }

    private void validateRequest(SecondHandCuponRequestDTO request) {
        if (request == null || request.shCuponCode() == null || request.shCuponCode().isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST, "shCuponCode es obligatorio");
        }
        if (request.shCuponCode().length() > 15) {
            throw new ResponseStatusException(BAD_REQUEST, "shCuponCode no puede superar 15 caracteres");
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
        if (request.shProductIds() == null || request.shProductIds().isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "Debe indicar al menos un producto para el cupon");
        }
    }

    private void validateProductsOwned(List<Long> shProductIds, Long adminId) {
        for (Long productId : distinct(shProductIds)) {
            SecondHandProduct product = secondHandProductRepository.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Second hand product not found: " + productId));
            if (!secondHandProductRepository.existsByIdAndOwner(productId, adminId)) {
                throw new ResponseStatusException(FORBIDDEN, "No puedes crear cupones sobre productos de otro admin");
            }
        }
    }

    private void requireOwner(SecondHandCupon cupon, Long adminId) {
        Long ownerId = cupon.getUserAdmin() != null ? cupon.getUserAdmin().getId() : null;
        if (ownerId == null || !ownerId.equals(adminId)) {
            throw new ResponseStatusException(FORBIDDEN, "No puedes modificar cupones de otro admin");
        }
    }

    private static LinkedHashSet<Long> distinct(List<Long> ids) {
        return ids == null ? new LinkedHashSet<>() : new LinkedHashSet<>(ids);
    }

    public void registerUsage(com.apiproject.entities.admin.ShCuponUsedByClients usage) {
        shCuponUsedByClientsRepository.save(usage);
    }

    // ================= Asignación de cupones SH a clientes =================

    @Transactional
    public List<ShProductCuponToClientResponseDTO> assignToClients(CuponAssignmentRequestDTO request, Long adminId) {
        if (request.cuponId() == null) {
            throw new ResponseStatusException(BAD_REQUEST, "cuponId es obligatorio");
        }
        SecondHandCupon cupon = secondHandCuponRepository.lockById(request.cuponId())
                .orElseThrow(() -> new ResourceNotFoundException("Second hand cupon not found: " + request.cuponId()));
        requireOwner(cupon, adminId);

        List<Long> productIds = secondHandProductCuponRepository.findShProductIdsByCuponId(cupon.getId());
        if (productIds.isEmpty()) {
            throw new ResponseStatusException(CONFLICT, "El cupon no tiene productos asociados");
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

        List<ShProductCuponToAClient> assignments = new ArrayList<>();
        for (Long clientId : targetClientIds) {
            UserClient client = clientRepository.getReferenceById(clientId);
            for (Long productId : productIds) {
                SecondHandProduct product = secondHandProductRepository.getReferenceById(productId);
                ShProductCuponToAClient assignment = new ShProductCuponToAClient();
                assignment.setClient(client);
                assignment.setCupon(cupon);
                assignment.setProduct(product);
                assignments.add(assignment);
            }
        }
        shProductCuponToAClientRepository.saveAll(assignments);
        return findAllAssignmentsByAdmin(adminId);
    }

    @Transactional(readOnly = true)
    public List<ShProductCuponToClientResponseDTO> findAllAssignmentsByAdmin(Long adminId) {
        return shProductCuponToAClientRepository.findAllByAdmin(adminId).stream()
                .map(this::toAssignmentDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ShProductCuponToClientResponseDTO> findAssignmentsByClient(Long adminId, Long clientId) {
        return shProductCuponToAClientRepository.findByAdminAndClient(adminId, clientId).stream()
                .map(this::toAssignmentDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ShProductCuponToClientResponseDTO> findAssignmentsByCupon(Long adminId, Long cuponId) {
        return shProductCuponToAClientRepository.findByAdminAndCupon(adminId, cuponId).stream()
                .map(this::toAssignmentDto)
                .toList();
    }

    @Transactional
    public void removeAssignment(Long assignmentId, Long adminId) {
        ShProductCuponToAClient assignment = shProductCuponToAClientRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found: " + assignmentId));
        requireOwner(assignment.getCupon(), adminId);
        shProductCuponToAClientRepository.deleteById(assignmentId);
    }

    @Transactional
    public void removeAllByCupon(Long cuponId, Long adminId) {
        SecondHandCupon cupon = secondHandCuponRepository.lockById(cuponId)
                .orElseThrow(() -> new ResourceNotFoundException("Second hand cupon not found: " + cuponId));
        requireOwner(cupon, adminId);
        shProductCuponToAClientRepository.deleteByCuponId(cuponId);
    }

    private ShProductCuponToClientResponseDTO toAssignmentDto(Object[] row) {
        return new ShProductCuponToClientResponseDTO(
                ((Number) row[0]).longValue(),
                ((Number) row[1]).longValue(),
                (String) row[2],
                (String) row[3],
                ((Number) row[4]).longValue(),
                (String) row[5],
                (Double) row[6],
                row[7] instanceof java.sql.Timestamp ts ? ts.toLocalDateTime() : (java.time.LocalDateTime) row[7],
                ((Number) row[8]).longValue(),
                (String) row[9]
        );
    }
}
