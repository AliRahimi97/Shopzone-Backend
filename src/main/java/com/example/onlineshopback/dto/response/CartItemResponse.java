package com.example.onlineshopback.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartItemResponse {

    private Integer cartItemId;
    private Integer productId;
    private String productName;
    private String brand;
    private String imageUrl;
    private Double price;
    private Integer discountPercent;
    private Double finalPrice;
    private Integer quantity;
    private Integer stock;
    private Double lineTotal;
}
