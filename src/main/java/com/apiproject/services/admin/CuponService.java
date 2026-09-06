package com.apiproject.services.admin;

import com.apiproject.DTOs.Admin.CuponAssignmentRequestDTO;
import com.apiproject.DTOs.Admin.CuponRequestDTO;
import com.apiproject.DTOs.Admin.CuponResponseDTO;
import com.apiproject.DTOs.Admin.ProductCuponToClientResponseDTO;
import com.apiproject.DTOs.General.CuponValidationResponseDTO;
import com.apiproject.entities.admin.Cupon;
import com.apiproject.entities.admin.CuponUsedByClients;
import com.apiproject.entities.admin.ProductCuponApplied;
import com.apiproject.entities.admin.ProductCuponToAClient;
import com.apiproject.entities.client.UserClient;
import com.apiproject.entities.general.Product;
import com.apiproject.exceptions.ResourceNotFoundException;
import com.apiproject.repositories.admin.CuponRepository;
import com.apiproject.repositories.admin.CuponUsedByClientsRepository;
import com.apiproject.repositories.admin.ProductCuponRepository;
import com.apiproject.repositories.admin.ProductCuponToAClientRepository;
import com.apiproject.repositories.admin.UserRepository;
import com.apiproject.repositories.client.ClientRepository;
import com.apiproject.repositories.general.ProductRepository;
import com.apiproject.repositories.projection.CuponAdminProjection;
import com.apiproject.repositories.projection.CuponUsageProjection;
import com.apiproject.repositories.projection.ProductCuponAssignmentProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
public class CuponService {
    private final CuponRepository cuponRepository;
    private final ProductCuponRepository productCuponRepository;
    private final CuponUsedByClientsRepository cuponUsedByClientsRepository;
    private final ProductCuponToAClientRepository productCuponToAClientRepository;
    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final ProductRepository productRepository;

    // ================= CRUD (ADMIN) =================

    @Transactional
    public CuponResponseDTO create(CuponRequestDTO request, Long adminId) {
        validateRequest(request);
        validateProductsOwned(request.productIds(), adminId);

        Cupon cupon = new Cupon();
        applyFields(cupon, request);
        cupon.setUserAdmin(userRepository.getReferenceById(adminId));
        Cupon saved = cuponRepository.save(cupon);
        replaceProducts(saved, request.productIds());

        return CuponResponseDTO.fromEntity(
                saved,
                adminId,
                productCuponRepository.findProductIdsByCuponId(saved.getId()));
    }

    @Transactional(readOnly = true)
    public List<CuponResponseDTO> findAllByOwner(Long adminId) {
        return cuponRepository.findAllByOwner(adminId).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public CuponResponseDTO update(Long id, CuponRequestDTO request, Long adminId) {
        validateRequest(request);

        Cupon cupon = cuponRepository.lockById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cupon not found: " + id));
        requireOwner(cupon, adminId);

        applyFields(cupon, request);
        if (request.productIds() != null) {
            productCuponRepository.deleteByCuponId(cupon.getId());
            replaceProducts(cupon, request.productIds());
        }

        return CuponResponseDTO.fromEntity(
                cupon,
                adminId,
                productCuponRepository.findProductIdsByCuponId(cupon.getId()));
    }

    @Transactional
    public void delete(Long id, Long adminId) {
        Cupon cupon = cuponRepository.lockById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cupon not found: " + id));
        requireOwner(cupon, adminId);
        productCuponRepository.deleteByCuponId(id);
        cuponRepository.delete(cupon);
    }

    // ============ Validación pública (CLIENT) ============

    @Transactional(readOnly = true)
    public CuponValidationResponseDTO validateForProduct(String code, Long productId) {
        return cuponRepository.findValidForProduct(code, productId, LocalDateTime.now())
                .map(usage -> new CuponValidationResponseDTO(
                        true,
                        "Cupon valido",
                        BigDecimal.valueOf(usage.getDiscount()),
                        usage.getCuponId()))
                .orElseGet(() -> CuponValidationResponseDTO.invalid(
                        "El cupon no existe, esta expirado, agoto sus usos o no aplica a este producto"));
    }

    /**
     * Resuelve un cupon para un carrito: debe existir, estar vigente con usos disponibles.
     * El cupon pertenece a un solo admin; aplica descuento únicamente a los productos del
     * carrito que le pertenecen a ese admin y que estan vinculados al cupon (elegibles).
     * No se exige que todos los productos del carrito sean del mismo vendedor; los de otros
     * admins (o no vinculados) se cobran completos. Bloquea la fila (FOR UPDATE).
     *
     * @param code          codigo del cupon
     * @param carritoProductIds ids de todos los productos del carrito
     * @param productoOwnerFn  funcion (productId -> adminId) que devuelve el dueño de cada producto
     * @return la resolucion con los ids de productos elegibles para descuento
     */
    @Transactional
    public CouponResolution resolveForCart(String code, List<Long> carritoProductIds,
                                           Function<Long, Long> productoOwnerFn) {
        Cupon cupon = cuponRepository.lockByCode(code)
                .orElseThrow(() -> new ResponseStatusException(CONFLICT, "El cupon no existe: " + code));

        if (cupon.getCuponDateLimit().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(CONFLICT, "El cupon esta expirado");
        }
        if (cupon.getQuantity() != null && cupon.getQuantity() <= 0) {
            throw new ResponseStatusException(CONFLICT, "El cupon agoto sus usos");
        }

        Long cuponOwnerId = cupon.getUserAdmin().getId();
        Set<Long> linkedProducts = new LinkedHashSet<>(
                productCuponRepository.findProductIdsByCuponId(cupon.getId()));

        // Elegibles = productos del carrito que pertenecen al admin del cupon Y estan vinculados.
        List<Long> elegibles = carritoProductIds.stream()
                .filter(id -> cuponOwnerId.equals(productoOwnerFn.apply(id)))
                .filter(linkedProducts::contains)
                .toList();

        if (elegibles.isEmpty()) {
            throw new ResponseStatusException(CONFLICT,
                    "El cupon no aplica a ningun producto del carrito de este vendedor");
        }

        // Si la cantidad limite no es ilimitada, solo se descuentan los primeros 'quantity' elegibles.
        Integer quantity = cupon.getQuantity();
        if (quantity != null && elegibles.size() > quantity) {
            elegibles = elegibles.subList(0, quantity);
        }

        return new CouponResolution(cupon.getId(), cupon.getDiscount(), cuponOwnerId, elegibles);
    }

    /** Descuenta un uso del cupon; lanza CONFLICT si ya no quedan usos. */
    @Transactional
    public void redeem(CouponResolution resolution) {
        int updatedRows = cuponRepository.decrementQuantity(resolution.cuponId());
        if (updatedRows == 0) {
            throw new ResponseStatusException(CONFLICT, "El cupon agoto sus usos");
        }
    }

    public record CouponResolution(Long cuponId, Double discountPercent, Long ownerId, List<Long> elegibleProductIds) {
    }

    // ================= Helpers =================

    private CuponResponseDTO toDto(CuponAdminProjection projection) {
        return CuponResponseDTO.fromProjection(projection, parseIds(projection.getAppliedProductIds()));
    }

    private void applyFields(Cupon cupon, CuponRequestDTO request) {
        cupon.setCuponCode(request.cuponCode().trim());
        cupon.setCuponDateLimit(request.cuponDateLimit());
        cupon.setDiscount(request.discount());
        cupon.setQuantity(request.quantity());
    }

    private void replaceProducts(Cupon cupon, List<Long> productIds) {
        List<ProductCuponApplied> links = new ArrayList<>();
        for (Long productId : distinct(productIds)) {
            ProductCuponApplied link = new ProductCuponApplied();
            link.setCupon(cupon);
            link.setProduct(productRepository.getReferenceById(productId));
            links.add(link);
        }
        productCuponRepository.saveAll(links);
    }

    private void validateRequest(CuponRequestDTO request) {
        if (request == null || request.cuponCode() == null || request.cuponCode().isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST, "cuponCode es obligatorio");
        }
        if (request.cuponCode().length() > 15) {
            throw new ResponseStatusException(BAD_REQUEST, "cuponCode no puede superar 15 caracteres");
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
        if (request.productIds() == null || request.productIds().isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "Debe indicar al menos un producto para el cupon");
        }
    }

    private void validateProductsOwned(List<Long> productIds, Long adminId) {
        for (Long productId : distinct(productIds)) {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));
            if (product.getUserAdmin() == null || !product.getUserAdmin().getId().equals(adminId)) {
                throw new ResponseStatusException(FORBIDDEN, "No puedes crear cupones sobre productos de otro admin");
            }
        }
    }

    private void requireOwner(Cupon cupon, Long adminId) {
        Long ownerId = cupon.getUserAdmin() != null ? cupon.getUserAdmin().getId() : null;
        if (ownerId == null || !ownerId.equals(adminId)) {
            throw new ResponseStatusException(FORBIDDEN, "No puedes modificar cupones de otro admin");
        }
    }

    static List<Long> parseIds(String csv) {
        if (csv == null || csv.isBlank()) return List.of();
        List<Long> ids = new ArrayList<>();
        for (String part : csv.split(",")) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) ids.add(Long.parseLong(trimmed));
        }
        return ids;
    }

    private static LinkedHashSet<Long> distinct(List<Long> ids) {
        return ids == null ? new LinkedHashSet<>() : new LinkedHashSet<>(ids);
    }

    // Referencia usada por SaleService al registrar el uso
    public void registerUsage(CuponUsedByClients usage) {
        cuponUsedByClientsRepository.save(usage);
    }

    // ================= Asignación de cupones a clientes =================

    @Transactional
    public List<ProductCuponToClientResponseDTO> assignToClients(CuponAssignmentRequestDTO request, Long adminId) {
        if (request.cuponId() == null) {
            throw new ResponseStatusException(BAD_REQUEST, "cuponId es obligatorio");
        }
        Cupon cupon = cuponRepository.lockById(request.cuponId())
                .orElseThrow(() -> new ResourceNotFoundException("Cupon not found: " + request.cuponId()));
        requireOwner(cupon, adminId);

        List<Long> productIds = productCuponRepository.findProductIdsByCuponId(cupon.getId());
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

        List<ProductCuponToAClient> assignments = new ArrayList<>();
        for (Long clientId : targetClientIds) {
            UserClient client = clientRepository.getReferenceById(clientId);
            for (Long productId : productIds) {
                Product product = productRepository.getReferenceById(productId);
                ProductCuponToAClient assignment = new ProductCuponToAClient();
                assignment.setClient(client);
                assignment.setCupon(cupon);
                assignment.setProduct(product);
                assignments.add(assignment);
            }
        }
        productCuponToAClientRepository.saveAll(assignments);
        return findAllAssignmentsByAdmin(adminId);
    }

    @Transactional(readOnly = true)
    public List<ProductCuponToClientResponseDTO> findAllAssignmentsByAdmin(Long adminId) {
        return productCuponToAClientRepository.findAllByAdmin(adminId).stream()
                .map(this::toAssignmentDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductCuponToClientResponseDTO> findAssignmentsByClient(Long adminId, Long clientId) {
        return productCuponToAClientRepository.findByAdminAndClient(adminId, clientId).stream()
                .map(this::toAssignmentDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductCuponToClientResponseDTO> findMyAssignments(Long clientId) {
        return productCuponToAClientRepository.findAllByClient(clientId).stream()
                .map(this::toAssignmentDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductCuponToClientResponseDTO> findAssignmentsByCupon(Long adminId, Long cuponId) {
        return productCuponToAClientRepository.findByAdminAndCupon(adminId, cuponId).stream()
                .map(this::toAssignmentDto)
                .toList();
    }

    @Transactional
    public void removeAssignment(Long assignmentId, Long adminId) {
        ProductCuponToAClient assignment = productCuponToAClientRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found: " + assignmentId));
        requireOwner(assignment.getCupon(), adminId);
        productCuponToAClientRepository.deleteById(assignmentId);
    }

    @Transactional
    public void removeAllByCupon(Long cuponId, Long adminId) {
        Cupon cupon = cuponRepository.lockById(cuponId)
                .orElseThrow(() -> new ResourceNotFoundException("Cupon not found: " + cuponId));
        requireOwner(cupon, adminId);
        productCuponToAClientRepository.deleteByCuponId(cuponId);
    }

    private ProductCuponToClientResponseDTO toAssignmentDto(ProductCuponAssignmentProjection p) {
        return new ProductCuponToClientResponseDTO(
                p.getId(),
                p.getClientId(),
                p.getClientName(),
                p.getClientEmail(),
                p.getCuponId(),
                p.getCuponCode(),
                p.getDiscount(),
                p.getCuponDateLimit(),
                p.getProductId(),
                p.getProductName()
        );
    }
}
