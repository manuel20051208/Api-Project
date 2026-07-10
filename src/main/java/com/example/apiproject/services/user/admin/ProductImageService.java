package com.example.apiproject.services.user.admin;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.apiproject.DTOs.Admin.ProductImageDTO;
import com.example.apiproject.entities.admin.ProductImage;
import com.example.apiproject.entities.general.Product;
import com.example.apiproject.exceptions.ResourceNotFoundException;
import com.example.apiproject.repositories.admin.ProductImageRepository;
import com.example.apiproject.services.general.ProductService;
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
public class ProductImageService {

    private final ProductImageRepository productImageRepository;
    private final ProductService productService;
    private final Cloudinary cloudinary;

    public ProductImageDTO uploadImage(Long productId, MultipartFile file, Long adminId) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Cannot upload an empty file.");
        }

        Map uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap("folder", "products")
        );

        String publicId = (String) uploadResult.get("public_id");
        String secureUrl = (String) uploadResult.get("secure_url");

        Product product = productService.findUser(productId, adminId);
        Long order = productImageRepository.countByProductId(productId) + 1;

        ProductImage image = new ProductImage();
        image.setFileName(file.getOriginalFilename());
        image.setFilePath(publicId);
        image.setUrl(secureUrl);
        image.setDisplayOrder(order);
        image.setProduct(product);
        image.setUserAdmin(product.getUserAdmin());

        ProductImage productImage = productImageRepository.save(image);
        return ProductImageDTO.fromEntity(productImage);
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
    }
}