package com.example.onlineshopback.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.onlineshopback.dto.UserListItemDTO;
import com.example.onlineshopback.dto.UserUpdateDTO;
import com.example.onlineshopback.dto.response.AdminResponseDTO;
import com.example.onlineshopback.dto.response.CustomerResponseDTO;
import com.example.onlineshopback.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    private UserService userService;

    @GetMapping("/admin/all")
    public ResponseEntity<List<UserListItemDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
    @GetMapping("/admin/{userId}")
    public ResponseEntity<UserListItemDTO> getUser(@PathVariable Integer userId){
        return ResponseEntity.ok(userService.getUserById(userId));
    }
    @GetMapping("/me")
    public ResponseEntity<Object> getProfile(HttpServletRequest request){
        
        return ResponseEntity.ok(userService.getUserProfile(request));
    }
    @PutMapping("/customer/{id}")
    public ResponseEntity<CustomerResponseDTO> updateCustomer(@PathVariable Integer id, @RequestBody UserUpdateDTO dto) {
        return ResponseEntity.ok(userService.updateCustomer(id, dto));
    }
    @PutMapping("/admin/{id}")
    public ResponseEntity<AdminResponseDTO> updateAdmin(@PathVariable Integer id, @RequestBody UserUpdateDTO dto) {
        return ResponseEntity.ok(userService.updateAdmin(id, dto));
    }



}
