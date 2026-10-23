package com.example.onlineshopback.dto.response;


import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WishlistItemResponse {
    private Long id;
    private Integer productId;
    private String productName;
    private String brand;
    private Double price;
    private Integer discountPercent;
    private String imageUrl;
    private Integer stock;
}
