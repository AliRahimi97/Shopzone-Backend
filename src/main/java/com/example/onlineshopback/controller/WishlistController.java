package com.example.onlineshopback.controller;

import com.example.onlineshopback.dto.response.WishlistItemResponse;
import com.example.onlineshopback.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @GetMapping
    public ResponseEntity<List<WishlistItemResponse>> get(@RequestParam Integer userId) {
        return ResponseEntity.ok(wishlistService.getWishlist(userId));
    }

    @PostMapping
    public ResponseEntity<List<WishlistItemResponse>> add(
            @RequestParam Integer userId,
            @RequestBody Map<String, Integer> body
    ) {
        Integer productId = body.get("productId");
        return ResponseEntity.ok(wishlistService.add(userId, productId));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<List<WishlistItemResponse>> remove(
            @RequestParam Integer userId,
            @PathVariable Integer productId
    ) {
        return ResponseEntity.ok(wishlistService.remove(userId, productId));
    }

    @GetMapping("/check")
    public ResponseEntity<Map<String, Boolean>> check(
            @RequestParam Integer userId,
            @RequestParam Integer productId
    ) {
        return ResponseEntity.ok(
                Map.of("inWishlist", wishlistService.isInWishlist(userId, productId))
        );
    }
}