package com.example.onlineshopback.service;

import com.example.onlineshopback.dto.response.WishlistItemResponse;
import com.example.onlineshopback.model.*;
import com.example.onlineshopback.repository.ProductRepository;
import com.example.onlineshopback.repository.UserRepository;
import com.example.onlineshopback.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<WishlistItemResponse> getWishlist(Integer userId) {
        return wishlistRepository.findByUserIdWithProduct(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public List<WishlistItemResponse> add(Integer userId, Integer productId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (!wishlistRepository.existsByUsersUserIdAndProductProductId(userId, productId)) {
            wishlistRepository.save(WishlistItem.builder()
                    .users(user)
                    .product(product)
                    .build());
        }
        return getWishlist(userId);
    }

    @Transactional
    public List<WishlistItemResponse> remove(Integer userId, Integer productId) {
        wishlistRepository.deleteByUsersUserIdAndProductProductId(userId, productId);
        return getWishlist(userId);
    }

    @Transactional(readOnly = true)
    public boolean isInWishlist(Integer userId, Integer productId) {
        return wishlistRepository.existsByUsersUserIdAndProductProductId(userId, productId);
    }

    @Transactional(readOnly = true)
    public long count(Integer userId) {
        return wishlistRepository.countByUsersUserId(userId);
    }

    private WishlistItemResponse toResponse(WishlistItem w) {
        Product p = w.getProduct();
        return WishlistItemResponse.builder()
                .id(w.getId())
                .productId(p.getProductId())
                .productName(p.getName())
                .brand(p.getBrand())
                .price(p.getPrice())
                .discountPercent(p.getDiscountPercent())
                .imageUrl(resolveImageUrl(p))
                .stock(p.getStock())
                .build();
    }

    private String resolveImageUrl(Product p) {
        if (p.getImages() != null && !p.getImages().isEmpty()) {
            return p.getImages().stream()
                    .filter(img -> Boolean.TRUE.equals(img.getPrimaryImage()))
                    .map(ProductImage::getImageUrl)
                    .findFirst()
                    .orElse(p.getImages().get(0).getImageUrl());
        }
        return p.getImageUrl();
    }
}