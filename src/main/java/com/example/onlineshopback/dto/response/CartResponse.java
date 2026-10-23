package com.example.onlineshopback.dto.response;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartResponse {

    private List<CartItemResponse> items;
    private Integer totalItems;
    private Double subtotal;
    private Double totalDiscount;
    private Double total;
}