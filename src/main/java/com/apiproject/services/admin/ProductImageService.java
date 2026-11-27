package com.apiproject.services.admin;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.apiproject.DTOs.Admin.ProductImageDTO;
import com.apiproject.DTOs.Admin.ReorderImagesRequestDTO;
import com.apiproject.entities.admin.ProductImage;
import com.apiproject.entities.general.Product;
import com.apiproject.exceptions.ResourceNotFoundException;
import com.apiproject.repositories.admin.ProductImageRepository;
import com.apiproject.services.general.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.FORBIDDEN;

@Service
@RequiredArgsConstructor
public class ProductImageService {

    private final ProductImageRepository productImageRepository;
    private final ProductService productService;
    private final Cloudinary cloudinary;

    /** Tope de imagenes por producto ({@code MAX_IMAGES_PER_PRODUCT}, default 8). */
    @Value("${app.images.max-per-product:8}")
    private int maxImagesPerProduct = 8;

    public ProductImageDTO uploadImage(Long productId, MultipartFile file, Long adminId) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Cannot upload an empty file.");
        }

        // Primero dueño y tope: si falla no se sube nada a Cloudinary (antes quedaban imagenes huerfanas)
        Product product = productService.findUser(productId, adminId);
        ImageOrderRules.checkCapacity(productImageRepository.countByProductId(productId), maxImagesPerProduct);
        Long order = productImageRepository.findMaxDisplayOrder(productId) + 1;

        Map uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap("folder", "products")
        );

        String publicId = (String) uploadResult.get("public_id");
        String secureUrl = (String) uploadResult.get("secure_url");

        ProductImage image = new ProductImage();
        image.setFileName(file.getOriginalFilename());
        image.setFilePath(publicId);
        image.setUrl(secureUrl);
        image.setDisplayOrder(order);
        image.setProduct(product);
        image.setUserAdmin(product.getUserAdmin());

        ProductImage productImage = productImageRepository.save(image);
        productService.evictProductCaches(productId, adminId);
        return ProductImageDTO.fromEntity(productImage);
    }

    /** Reordena las imagenes del producto segun {@code request.imageIds()} (display_order 1..n). */
    @Transactional
    public List<ProductImageDTO> reorder(ReorderImagesRequestDTO request, Long adminId) {
        Long productId = request.productId();
        productService.findUser(productId, adminId); // 403 si no es el dueño

        List<ProductImage> images = productImageRepository.findByProductIdOrderByDisplayOrder(productId);
        ImageOrderRules.checkSameImages(images.stream().map(ProductImage::getId).toList(), request.imageIds());

        Map<Long, ProductImage> byId = new HashMap<>();
        images.forEach(image -> byId.put(image.getId(), image));
        List<ProductImage> ordered = new ArrayList<>();
        long position = 1;
        for (Long imageId : request.imageIds()) {
            ProductImage image = byId.get(imageId);
            image.setDisplayOrder(position++);
            ordered.add(image);
        }
        productImageRepository.saveAll(ordered);
        productService.evictProductCaches(productId, adminId);
        return ordered.stream().map(ProductImageDTO::fromEntity).toList();
    }

    public List<ProductImageDTO> getImagesByProductId(Long productId) {
        return productImageRepository.findByProductIdOrderByDisplayOrder(productId)
                .stream().map(ProductImageDTO::fromEntity)
                .toList();
    }

    @Transactional
    public void deleteImage(Long imageId, Long adminId) throws IOException {
        ProductImage image = productImageRepository.findDeleteInfoById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Image not found: " + imageId));

        if (image.getId() == null || !image.getOwnerId().equals(adminId)) {
            throw new ResponseStatusException(FORBIDDEN, "No puedes eliminar esta imagen...");
        }

        cloudinary.uploader().destroy(image.getFilePath(), ObjectUtils.emptyMap());

        productImageRepository.deleteByImageId(imageId);
        productService.evictProductCaches(image.getProduct() != null ? image.getProduct().getId() : null, adminId);
    }
}