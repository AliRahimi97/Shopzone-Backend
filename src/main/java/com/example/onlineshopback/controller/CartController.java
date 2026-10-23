package com.example.onlineshopback.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.onlineshopback.dto.request.CartItemRequest;
import com.example.onlineshopback.dto.request.UpdateCartItemRequest;
import com.example.onlineshopback.dto.response.CartResponse;
import com.example.onlineshopback.service.CartService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/cart")
@RequiredArgsConstructor
public class CartController {
    
    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartResponse> getCart(@RequestParam Integer userId) {
        return ResponseEntity.ok(cartService.getCart(userId));
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponse> addItem(@RequestParam Integer userId, @Valid @RequestBody CartItemRequest request){
        return ResponseEntity.ok(cartService.addItem(userId, request));
    }

    @PutMapping("/items/{cartItemId}")
    public ResponseEntity<CartResponse> updateItem(@RequestParam Integer userId, 
        @PathVariable Integer cartItemId, 
        @Valid @RequestBody UpdateCartItemRequest request) {
            return ResponseEntity.ok(cartService.updateItem(userId, cartItemId, request));
        }
    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<CartResponse> removeItem(@RequestParam Integer userId,
        @PathVariable Integer cartItemId) {
            return ResponseEntity.ok(cartService.removeItem(userId, cartItemId));
        }
    
    @DeleteMapping
    public ResponseEntity<Void> clearChart(@RequestParam Integer userId){
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
    

}
