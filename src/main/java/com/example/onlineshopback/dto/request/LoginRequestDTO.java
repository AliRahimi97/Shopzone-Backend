package com.example.onlineshopback.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
public class LoginRequestDTO {
    
    @NotBlank(message = "Email is Required")
    @Email(message = "Enter a valid email")
    private String email;

    @NotBlank(message = "password is required")
    private String password;
}
