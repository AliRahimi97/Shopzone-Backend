package com.example.onlineshopback.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateOrderStatusRequest {
    @NotBlank
    private String status; // PENDING, PAID, SHIPPED, DELIVERED, CANCELLED
}