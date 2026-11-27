package com.apiproject.services.admin;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.apiproject.DTOs.Admin.ShProductImageDTO;
import com.apiproject.DTOs.Admin.ReorderImagesRequestDTO;
import com.apiproject.entities.admin.SecondHandProductImage;
import com.apiproject.entities.general.SecondHandProduct;
import com.apiproject.exceptions.ResourceNotFoundException;
import com.apiproject.repositories.admin.SecondHandProductImagesRepository;
import com.apiproject.services.general.SecondHandProductService;
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
public class SecondHandProductImageService {

    private final SecondHandProductImagesRepository secondHandProductImagesRepository;
    private final SecondHandProductService secondHandProductService;
    private final Cloudinary cloudinary;

    /** Tope de imagenes por producto ({@code MAX_IMAGES_PER_PRODUCT}, default 8). */
    @Value("${app.images.max-per-product:8}")
    private int maxImagesPerProduct = 8;

    public ShProductImageDTO uploadImage(Long shProductId, MultipartFile file, Long adminId) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Cannot upload an empty file.");
        }

        // Primero dueño y tope: si falla no se sube nada a Cloudinary (antes quedaban imagenes huerfanas)
        SecondHandProduct product = secondHandProductService.findOwnedReference(shProductId, adminId);
        ImageOrderRules.checkCapacity(secondHandProductImagesRepository.countByProductId(shProductId), maxImagesPerProduct);
        Long order = secondHandProductImagesRepository.findMaxDisplayOrder(shProductId) + 1;

        Map uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap("folder", "secondhand")
        );

        String publicId = (String) uploadResult.get("public_id");
        String secureUrl = (String) uploadResult.get("secure_url");

        SecondHandProductImage image = new SecondHandProductImage();
        image.setFileName(file.getOriginalFilename());
        image.setFilePath(publicId);
        image.setUrl(secureUrl);
        image.setDisplayOrder(order);
        image.setProduct(product);
        image.setUserAdmin(product.getUserAdmin());

        return ShProductImageDTO.fromEntity(secondHandProductImagesRepository.save(image));
    }

    /** Reordena las imagenes del producto segun {@code request.imageIds()} (display_order 1..n). */
    @Transactional
    public List<ShProductImageDTO> reorder(ReorderImagesRequestDTO request, Long adminId) {
        Long productId = request.productId();
        secondHandProductService.findOwnedReference(productId, adminId); // 403 si no es el dueño

        List<SecondHandProductImage> images = secondHandProductImagesRepository.findByProductIdOrderByDisplayOrder(productId);
        ImageOrderRules.checkSameImages(images.stream().map(SecondHandProductImage::getId).toList(), request.imageIds());

        Map<Long, SecondHandProductImage> byId = new HashMap<>();
        images.forEach(image -> byId.put(image.getId(), image));
        List<SecondHandProductImage> ordered = new ArrayList<>();
        long position = 1;
        for (Long imageId : request.imageIds()) {
            SecondHandProductImage image = byId.get(imageId);
            image.setDisplayOrder(position++);
            ordered.add(image);
        }
        secondHandProductImagesRepository.saveAll(ordered);
        return ordered.stream().map(ShProductImageDTO::fromEntity).toList();
    }

    public List<ShProductImageDTO> getImagesByProductId(Long shProductId) {
        return secondHandProductImagesRepository.findByProductIdOrderByDisplayOrder(shProductId)
                .stream().map(ShProductImageDTO::fromEntity)
                .toList();
    }

    @Transactional
    public void deleteImage(Long imageId, Long adminId) throws IOException {
        SecondHandProductImage image = secondHandProductImagesRepository.findDeleteInfoById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Image not found: " + imageId));

        if (image.getId() == null || !image.getOwnerId().equals(adminId)) {
            throw new ResponseStatusException(FORBIDDEN, "No puedes eliminar esta imagen...");
        }

        cloudinary.uploader().destroy(image.getFilePath(), ObjectUtils.emptyMap());

        secondHandProductImagesRepository.deleteByImageId(imageId);
    }
}
