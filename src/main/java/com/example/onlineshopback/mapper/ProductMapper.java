package com.example.onlineshopback.mapper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.example.onlineshopback.dto.ProductDTO;
import com.example.onlineshopback.dto.ProductImageDTO;
import com.example.onlineshopback.model.Product;
import com.example.onlineshopback.model.ProductImage;

public class ProductMapper {

    public static Product toEntityProduct(ProductDTO dto) {
        Product product = new Product();

        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setCategory(dto.getCategory());
        product.setBrand(dto.getBrand());
        product.setDiscountPercent(dto.getDiscountPercent());
        product.setActive(dto.getActive() != null ? dto.getActive() : true);

        return product;
    }

    public static ProductDTO toProductDTO(Product product) {
        if (product == null) {
            return null;
        }

        ProductDTO dto = new ProductDTO();

        dto.setProductId(product.getProductId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setStock(product.getStock());
        dto.setCategory(product.getCategory());
        dto.setBrand(product.getBrand());
        dto.setDiscountPercent(product.getDiscountPercent());
        dto.setRating(product.getRating());
        dto.setReviewCount(product.getReviewCount());
        dto.setActive(product.getActive());

        // --- بخش گالری ---
        if (product.getImages() != null && !product.getImages().isEmpty()) {

            // لیست کامل عکس‌ها
            List<ProductImageDTO> imageDTOs = product.getImages().stream()
                    .map(ProductMapper::toImageDTO)
                    .collect(Collectors.toList());

            dto.setImages(imageDTOs);

            // عکس اصلی (primary) برای سازگاری با فرانت قدیمی
            String primaryUrl = product.getImages().stream()
                    .filter(img -> Boolean.TRUE.equals(img.getPrimaryImage()))
                    .findFirst()
                    .map(ProductImage::getImageUrl)
                    .orElse(product.getImages().get(0).getImageUrl());

            dto.setImageUrl(primaryUrl);

        } else {
            dto.setImages(Collections.emptyList());
            dto.setImageUrl(null);
        }

        return dto;
    }

    public static ProductImageDTO toImageDTO(ProductImage image) {
        if (image == null) {
            return null;
        }

        ProductImageDTO dto = new ProductImageDTO();
        dto.setId(image.getId());
        dto.setImageUrl(image.getImageUrl());
        dto.setDisplayOrder(image.getDisplayOrder());
        dto.setPrimaryImage(image.getPrimaryImage());

        return dto;
    }
}