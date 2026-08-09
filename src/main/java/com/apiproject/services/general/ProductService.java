package com.apiproject.services.general;

import com.apiproject.DTOs.Admin.UserAdminDTO;
import com.apiproject.DTOs.General.ProductResponseDTO;
import com.apiproject.config.CacheConstants;
import com.apiproject.entities.admin.UserAdmin;
import com.apiproject.entities.general.Product;
import com.apiproject.exceptions.ResourceNotFoundException;
import com.apiproject.repositories.admin.UserRepository;
import com.apiproject.repositories.general.ProductRepository;
import io.micrometer.common.lang.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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
    @Cacheable(value = CacheConstants.PRODUCTS_BY_ADMIN, key = "#adminId", sync = true)
    public List<ProductResponseDTO> findAll(Long adminId) {
        return productRepository.findAllByUserAdminIdWithImages(adminId).stream()
                .map(ProductResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = CacheConstants.PRODUCTS_ACTIVE_WITH_IMAGES, key = "'all'", sync = true)
    public List<ProductResponseDTO> findAllActiveWithImages() {
        return productRepository.findAllActiveWithImages().stream()
                .map(ProductResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = CacheConstants.PRODUCTS_BY_CATEGORY, key = "#category.toLowerCase()", sync = true)
    public List<ProductResponseDTO> findAllByCategoryIgnoreCase(String category) {
        return productRepository.findAllByCategoryIgnoreCaseWithImages(category).stream()
                .map(ProductResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = CacheConstants.PRODUCT_BY_ID, key = "#id", sync = true)
    public List<ProductResponseDTO> findAllById(Long id) {
        return productRepository.findByIdWithImages(id)
                .map(ProductResponseDTO::fromEntity)
                .stream()
                .toList();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = CacheConstants.PRODUCT_BY_NAME, key = "#name.toLowerCase()", sync = true)
    public ProductResponseDTO findByNameIgnoreCase(String name) {
        Product product = productRepository.findByNameIgnoreCaseWithImages(name)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + name));
        return ProductResponseDTO.fromEntity(product);
    }

    @Caching(evict = {
            @CacheEvict(value = CacheConstants.PRODUCTS_BY_ADMIN, key = "#adminId"),
            @CacheEvict(value = CacheConstants.PRODUCTS_WITH_IMAGES_BY_ADMIN, key = "#adminId"),
            @CacheEvict(value = CacheConstants.PRODUCTS_ACTIVE_WITH_IMAGES, allEntries = true),
            @CacheEvict(value = CacheConstants.PRODUCTS_BY_CATEGORY, allEntries = true),
            @CacheEvict(value = CacheConstants.PRODUCTS_ACTIVE_PAGE, allEntries = true)
    })
    public void save(Product product, Long adminId) {
        UserAdmin userAdmin = userRepository.getReferenceById(adminId);
        product.setUserAdmin(userAdmin);
        productRepository.save(product);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = CacheConstants.PRODUCTS_ACTIVE_PAGE, key = "#pageSize", sync = true)
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

    @Caching(evict = {
            @CacheEvict(value = CacheConstants.PRODUCTS_BY_ADMIN, key = "#adminId"),
            @CacheEvict(value = CacheConstants.PRODUCTS_WITH_IMAGES_BY_ADMIN, key = "#adminId"),
            @CacheEvict(value = CacheConstants.PRODUCT_BY_ID, key = "#id"),
            @CacheEvict(value = CacheConstants.PRODUCT_ADMIN, key = "#id"),
            @CacheEvict(value = CacheConstants.PRODUCT_BY_NAME, allEntries = true),
            @CacheEvict(value = CacheConstants.PRODUCTS_ACTIVE_WITH_IMAGES, allEntries = true),
            @CacheEvict(value = CacheConstants.PRODUCTS_BY_CATEGORY, allEntries = true),
            @CacheEvict(value = CacheConstants.PRODUCTS_ACTIVE_PAGE, allEntries = true)
    })
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

    @Caching(evict = {
            @CacheEvict(value = CacheConstants.PRODUCTS_BY_ADMIN, key = "#adminId"),
            @CacheEvict(value = CacheConstants.PRODUCTS_WITH_IMAGES_BY_ADMIN, key = "#adminId"),
            @CacheEvict(value = CacheConstants.PRODUCT_BY_ID, key = "#id"),
            @CacheEvict(value = CacheConstants.PRODUCT_ADMIN, key = "#id"),
            @CacheEvict(value = CacheConstants.PRODUCT_BY_NAME, allEntries = true),
            @CacheEvict(value = CacheConstants.PRODUCTS_ACTIVE_WITH_IMAGES, allEntries = true),
            @CacheEvict(value = CacheConstants.PRODUCTS_BY_CATEGORY, allEntries = true),
            @CacheEvict(value = CacheConstants.PRODUCTS_ACTIVE_PAGE, allEntries = true)
    })
    public void deleteProductSafe(Long id, Long adminId) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
        validateOwner(product, adminId);
        product.setActive(false);
        productRepository.save(product);
    }

    public Product findUser(Long id, Long adminId) {
        if (!productRepository.existsByIdAndUserAdmin_Id(id, adminId)) {
            throw new ResponseStatusException(
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

    @Transactional(readOnly = true)
    @Cacheable(value = CacheConstants.PRODUCTS_WITH_IMAGES_BY_ADMIN, key = "#adminId", sync = true)
    public List<ProductResponseDTO> findAllWithImages(Long adminId) {
        return productRepository.findAllWithImagesByUserAdminId(adminId).stream()
                .map(ProductResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = CacheConstants.PRODUCT_ADMIN, key = "#productId", sync = true)
    public UserAdminDTO showUserAdmin(Long productId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product not found"));

        if (!product.isActive()) {
            throw new IllegalArgumentException("Product is not active");
        }

        return UserAdminDTO.fromEntity(product.getUserAdmin());
    }
}
