package com.example.onlineshopback.dto.response;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

@Data 
@Builder
public class OrderItemResponse {
    
    private Long orderItemId;
    private Integer productId;
    private String productName;
    private String imageUrl;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal lineTotal;
    

}
