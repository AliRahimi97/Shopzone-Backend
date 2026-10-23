package com.example.onlineshopback.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.onlineshopback.dto.CheckoutRequest;
import com.example.onlineshopback.dto.response.OrderResponse;
import com.example.onlineshopback.model.OrderStatus;
import com.example.onlineshopback.service.OrderSevice;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    
    private final OrderSevice orderService;

    @PostMapping("/checkout")
    public ResponseEntity<OrderResponse> checkout(@RequestParam Integer userId,
         @Valid @RequestBody CheckoutRequest request) {

            return ResponseEntity.ok(orderService.checkout(userId, request));
        
    }

    /** List my orders */
    @GetMapping
    public ResponseEntity<List<OrderResponse>> myOrders(@RequestParam Integer userId) {
        return ResponseEntity.ok(orderService.getMyOrders(userId));
    }
     @GetMapping("/admin/all")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    } 
    
    @PutMapping("/admin/{orderId}/status")
    public ResponseEntity<OrderResponse> updateStatus(
        @PathVariable Long orderId,
        @RequestParam String status
    ) {
        OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
        return ResponseEntity.ok(orderService.updateStatus(orderId, orderStatus));
    }

    /** Order detail */
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(
            @RequestParam Integer userId,
            @PathVariable Long orderId
    ) {
        return ResponseEntity.ok(orderService.getOrder(userId, orderId));
    }

   
    

}
