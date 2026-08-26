package com.apiproject.services.admin;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.apiproject.DTOs.Admin.ShProductImageDTO;
import com.apiproject.entities.admin.SecondHandProductImage;
import com.apiproject.entities.general.SecondHandProduct;
import com.apiproject.exceptions.ResourceNotFoundException;
import com.apiproject.repositories.admin.SecondHandProductImagesRepository;
import com.apiproject.services.general.SecondHandProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.FORBIDDEN;

@Service
@RequiredArgsConstructor
public class SecondHandProductImageService {

    private final SecondHandProductImagesRepository secondHandProductImagesRepository;
    private final SecondHandProductService secondHandProductService;
    private final Cloudinary cloudinary;

    public ShProductImageDTO uploadImage(Long shProductId, MultipartFile file, Long adminId) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Cannot upload an empty file.");
        }

        Map uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap("folder", "secondhand")
        );

        String publicId = (String) uploadResult.get("public_id");
        String secureUrl = (String) uploadResult.get("secure_url");

        SecondHandProduct product = secondHandProductService.findOwnedReference(shProductId, adminId);
        Long order = secondHandProductImagesRepository.countByProductId(shProductId) + 1;

        SecondHandProductImage image = new SecondHandProductImage();
        image.setFileName(file.getOriginalFilename());
        image.setFilePath(publicId);
        image.setUrl(secureUrl);
        image.setDisplayOrder(order);
        image.setProduct(product);
        image.setUserAdmin(product.getUserAdmin());

        return ShProductImageDTO.fromEntity(secondHandProductImagesRepository.save(image));
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
