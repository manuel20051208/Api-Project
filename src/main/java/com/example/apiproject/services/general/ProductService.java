package com.example.apiproject.services.general;

import com.example.apiproject.DTOs.General.ProductResponseDTO;
import com.example.apiproject.entities.admin.UserAdmin;
import com.example.apiproject.entities.general.Product;
import com.example.apiproject.exceptions.ResourceNotFoundException;
import com.example.apiproject.repositories.admin.UserRepository;
import com.example.apiproject.repositories.general.ProductRepository;
import io.micrometer.common.lang.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.FORBIDDEN;

@Service
@RequiredArgsConstructor
public class ProductService {
    public final ProductRepository productRepository;
    public final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<ProductResponseDTO> findAll(Long adminId) {
        return productRepository.findAllByUserAdminIdWithImages(adminId).stream()
                .map(ProductResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductResponseDTO> findAllActiveWithImages() {
        return productRepository.findAllActiveWithImages().stream()
                .map(ProductResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductResponseDTO> findAllByCategoryIgnoreCase(String category) {
        return productRepository.findAllByCategoryIgnoreCaseWithImages(category).stream()
                .map(ProductResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductResponseDTO> findAllById(Long id) {
        return productRepository.findByIdWithImages(id)
                .map(ProductResponseDTO::fromEntity)
                .stream()
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductResponseDTO findByNameIgnoreCase(String name) {
        Product product = productRepository.findByNameIgnoreCaseWithImages(name)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + name));
        return ProductResponseDTO.fromEntity(product);
    }

    public void save(Product product, Long adminId) {
        UserAdmin userAdmin = userRepository.getReferenceById(adminId);
        product.setUserAdmin(userAdmin);
        productRepository.save(product);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponseDTO> findByActiveTrue(int pageSize) {
        Pageable firstPageByName = PageRequest.of(0, pageSize, Sort.by("name").ascending());
        Page<Long> idPage = productRepository.findActiveProductIds(firstPageByName);

        if (idPage.isEmpty()) {
            return Page.empty(firstPageByName);
        }

        Map<Long, Product> productsById = productRepository.findAllWithImagesByIdIn(idPage.getContent()).stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        List<ProductResponseDTO> content = idPage.getContent().stream()
                .map(productsById::get)
                .filter(Objects::nonNull)
                .map(ProductResponseDTO::fromEntity)
                .toList();

        return new PageImpl<>(content, firstPageByName, idPage.getTotalElements());
    }

    @Transactional(readOnly = true)
    public Product findUser(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
    }

    public ProductResponseDTO updateProduct(Long id, @NonNull Product product, Long adminId) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
        validateOwner(existing, adminId);

        Optional.ofNullable(product.getName()).ifPresent(existing::setName);
        Optional.ofNullable(product.getPrice()).ifPresent(existing::setPrice);
        Optional.ofNullable(product.getStock()).ifPresent(existing::setStock);
        Optional.ofNullable(product.getCategory()).ifPresent(existing::setCategory);
        existing.setDescription(product.getDescription());

        return ProductResponseDTO.fromEntity(productRepository.save(existing));
    }

    public void deleteProductSafe(Long id, Long adminId) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
        validateOwner(product, adminId);
        product.setActive(false);
        productRepository.save(product);
    }

    public Product findUser(Long id, Long adminId) {
        if (!productRepository.existsByIdAndUserAdmin_Id(id, adminId)) {
            throw new org.springframework.web.server.ResponseStatusException(
                    FORBIDDEN,
                    "No puedes modificar productos de otro usuario admin");
        }
        return productRepository.getReferenceById(id);
    }

    private void validateOwner(Product product, Long adminId) {
        Long ownerId = product.getUserAdmin() != null ? product.getUserAdmin().getId() : null;
        if (ownerId == null || !ownerId.equals(adminId)) {
            throw new org.springframework.web.server.ResponseStatusException(
                    FORBIDDEN,
                    "No puedes modificar productos de otro usuario admin");
        }
    }
}
