package com.example.onlineshopback.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.onlineshopback.dto.request.CartItemRequest;
import com.example.onlineshopback.dto.request.UpdateCartItemRequest;
import com.example.onlineshopback.dto.response.CartItemResponse;
import com.example.onlineshopback.dto.response.CartResponse;
import com.example.onlineshopback.model.CartItem;
import com.example.onlineshopback.model.Product;
import com.example.onlineshopback.model.ProductImage;
import com.example.onlineshopback.model.Users;
import com.example.onlineshopback.repository.CartItemRepository;
import com.example.onlineshopback.repository.ProductRepository;
import com.example.onlineshopback.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public CartResponse getCart(Integer userId) {
        List<CartItem> items = cartItemRepository.findByUserIdWithProduct(userId);
        return toCartResponse(items);
    }

    @Transactional
    public CartResponse addItem(Integer userId, CartItemRequest request) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getActive() != null && !product.getActive()) {
            throw new RuntimeException("Product is not active");
        }

        if (product.getStock() == null || product.getStock() < request.getQuantity()) {
            throw new RuntimeException("Not enough stock");
        }

        CartItem cartItem = cartItemRepository
                .findByUsersUserIdAndProductProductId(userId, request.getProductId())
                .orElse(null);

        if (cartItem == null) {
            cartItem = new CartItem();
            cartItem.setUsers(user);
            cartItem.setProduct(product);
            cartItem.setQuantity(request.getQuantity());
        } else {
            int newQty = cartItem.getQuantity() + request.getQuantity();
            if (product.getStock() < newQty) {
                throw new RuntimeException("Not enough stock");
            }
            cartItem.setQuantity(newQty);
        }

        cartItemRepository.save(cartItem);
        return getCart(userId);
    }

    @Transactional
    public CartResponse updateItem(Integer userId, Integer cartItemId, UpdateCartItemRequest request) {
        CartItem cartItem = cartItemRepository
                .findByCartItemIdAndUsersUserId(cartItemId, userId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        Product product = cartItem.getProduct();
        if (product.getStock() == null || product.getStock() < request.getQuantity()) {
            throw new RuntimeException("Not enough stock");
        }

        cartItem.setQuantity(request.getQuantity());
        cartItemRepository.save(cartItem);
        return getCart(userId);
    }

    @Transactional
    public CartResponse removeItem(Integer userId, Integer cartItemId) {
        CartItem cartItem = cartItemRepository
                .findByCartItemIdAndUsersUserId(cartItemId, userId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        cartItemRepository.delete(cartItem);
        return getCart(userId);
    }

    @Transactional
    public void clearCart(Integer userId) {
        cartItemRepository.deleteByUsersUserId(userId);
    }

    private CartResponse toCartResponse(List<CartItem> items) {
        List<CartItemResponse> lines = new ArrayList<>();
        double subtotal = 0;
        double totalDiscount = 0;
        int totalItems = 0;

        for (CartItem item : items) {
            Product p = item.getProduct();
            double price = p.getPrice() != null ? p.getPrice() : 0;
            int discount = p.getDiscountPercent() != null ? p.getDiscountPercent() : 0;
            double finalPrice = price * (1 - discount / 100.0);
            int qty = item.getQuantity() != null ? item.getQuantity() : 0;
            double lineTotal = finalPrice * qty;

            subtotal += price * qty;
            totalDiscount += (price - finalPrice) * qty;
            totalItems += qty;

            lines.add(CartItemResponse.builder()
                    .cartItemId(item.getCartItemId())
                    .productId(p.getProductId())
                    .productName(p.getName())
                    .brand(p.getBrand())
                    .imageUrl(resolveImageUrl(p))
                    .price(price)
                    .discountPercent(discount)
                    .finalPrice(round(finalPrice))
                    .quantity(qty)
                    .stock(p.getStock())
                    .lineTotal(round(lineTotal))
                    .build());
        }

        return CartResponse.builder()
                .items(lines)
                .totalItems(totalItems)
                .subtotal(round(subtotal))
                .totalDiscount(round(totalDiscount))
                .total(round(subtotal - totalDiscount))
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

    private double round(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}