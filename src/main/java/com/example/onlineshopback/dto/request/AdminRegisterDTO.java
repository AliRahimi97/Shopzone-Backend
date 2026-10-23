package com.example.onlineshopback.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminRegisterDTO {

    
    private Integer userId;
    
    @NotBlank(message = "First name is required! ")
    private String firstName;

    @NotBlank(message = "Last name is required! ")
    private String lastName;

    @Email(message = "Enter a valid email address")
    private String email;

    @Pattern(regexp = "[6789]{1}[0-9]{9}", message = "Enter a valid mobile number")
    private String mobile;

    @Pattern(regexp = "[A-Za-z0-9!@#$%^&*_]{8,15}", message = "Password must be 8-15 characters")
    private String password;


    @NotBlank(message = "Secret admin code is required")
    private String adminCode;
    
}
