package com.example.onlineshopback.controller;

import com.example.onlineshopback.dto.request.AdminRegisterDTO;
import com.example.onlineshopback.dto.request.CustomerRegisterDTO;
import com.example.onlineshopback.dto.request.LoginRequestDTO;
import com.example.onlineshopback.dto.response.LoginResponseDTO;
import com.example.onlineshopback.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class AuthController {

    
    private final AuthService service;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO loginRequestDTO) {
        
        LoginResponseDTO response = service.authenticate(loginRequestDTO);
        
        if (response.getToken() == null) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register/customer")
    public ResponseEntity<LoginResponseDTO> registerCustomer(@RequestBody @Valid CustomerRegisterDTO register){
        LoginResponseDTO customer = service.registerCustomer(register);

        return ResponseEntity.ok(customer);

    }

    @PostMapping("/register/admin")
    public ResponseEntity<LoginResponseDTO> registerAdmin(@RequestBody @Valid AdminRegisterDTO adminRegisterDTO) {
        LoginResponseDTO admin = service.registerAdmin(adminRegisterDTO);

        return ResponseEntity.ok(admin);
    }
}
