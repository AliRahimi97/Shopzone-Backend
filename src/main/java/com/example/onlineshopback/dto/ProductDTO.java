package com.example.onlineshopback.dto;

import java.util.List;

import com.example.onlineshopback.model.CategoryEnum;

import lombok.Data;

@Data
public class ProductDTO {

    private Integer productId;

    private String name;

    private String description;

    private Double price;

    private Integer stock;

    private CategoryEnum category;

    private String brand;

    private Integer discountPercent;

    private Double rating;

    private Integer reviewCount;

    private Boolean active;

    private List<ProductImageDTO> images;
    private String imageUrl;
}