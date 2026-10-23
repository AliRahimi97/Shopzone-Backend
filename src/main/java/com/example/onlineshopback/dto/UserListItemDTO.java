package com.example.onlineshopback.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserListItemDTO {
    private Integer userId;
    private String firstName;
    private String lastName;
    private String email;
    private String mobile;
    private String role;
    private String createdOn; // یا LocalDateTime
}