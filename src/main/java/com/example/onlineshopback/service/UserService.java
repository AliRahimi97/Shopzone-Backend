package com.example.onlineshopback.service;

import java.util.List;

import com.example.onlineshopback.dto.UserListItemDTO;
import com.example.onlineshopback.dto.UserUpdateDTO;
import com.example.onlineshopback.dto.request.AdminRegisterDTO;
import com.example.onlineshopback.dto.request.CustomerRegisterDTO;
import com.example.onlineshopback.dto.request.LoginRequestDTO;
import com.example.onlineshopback.dto.response.AdminResponseDTO;
import com.example.onlineshopback.dto.response.CustomerResponseDTO;
import com.example.onlineshopback.dto.response.LoginResponseDTO;

import jakarta.servlet.http.HttpServletRequest;
public interface UserService {
    

    CustomerResponseDTO registerCustomer(CustomerRegisterDTO customerDTO);
    AdminResponseDTO registerAdmin(AdminRegisterDTO adminDTO);
    LoginResponseDTO loginUser(LoginRequestDTO loginDTO);
    Object getUserProfile(HttpServletRequest request);
    CustomerResponseDTO updateCustomer(Integer userId, UserUpdateDTO updateDTO);
    AdminResponseDTO updateAdmin(Integer userId, UserUpdateDTO updateDTO);
    List<UserListItemDTO> getAllUsers();
    UserListItemDTO getUserById(Integer userId);


}
