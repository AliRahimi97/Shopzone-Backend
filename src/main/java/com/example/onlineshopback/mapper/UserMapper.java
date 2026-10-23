package com.example.onlineshopback.mapper;

import java.time.LocalDateTime;

import com.example.onlineshopback.dto.UserUpdateDTO;
import com.example.onlineshopback.dto.request.AdminRegisterDTO;
import com.example.onlineshopback.dto.request.CustomerRegisterDTO;
import com.example.onlineshopback.dto.response.AdminResponseDTO;
import com.example.onlineshopback.dto.response.CustomerResponseDTO;
import com.example.onlineshopback.model.Role;
import com.example.onlineshopback.model.Users;

public class UserMapper {
    
    public static Users toEntityFromCustomerRegister(CustomerRegisterDTO dto) {
        Users user = new Users();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setMobile(dto.getMobile());
        user.setPassword(dto.getPassword());
        user.setRole(Role.CUSTOMER);
        user.setCreatedOn(LocalDateTime.now());

        return user;
        
    }
    public static Users toEntityFromAdminRegister(AdminRegisterDTO dto) {
        Users user = new Users();

        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setMobile(dto.getMobile());
        user.setPassword(dto.getPassword());
        user.setRole(Role.ADMIN);
        user.setCreatedOn(LocalDateTime.now());

        return user;
    }

    public static CustomerResponseDTO toCustomerResponseDTO(Users user) {
        CustomerResponseDTO dto = new CustomerResponseDTO();

        dto.setId(user.getUserId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setMobile(user.getMobile());
        dto.setCreatedOn(user.getCreatedOn());
        
        return dto;

    }
    public static AdminResponseDTO toAdminResponseDTO(Users user) {
        AdminResponseDTO admin = new AdminResponseDTO();
        admin.setId(user.getUserId());
        admin.setFirstName(user.getFirstName());
        admin.setLastName(user.getLastName());
        admin.setEmail(user.getEmail());
        admin.setMobile(user.getMobile());
        

        return admin;

    }

    public static void updateUserFromDTO(Users user, UserUpdateDTO dto) {
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setMobile(dto.getMobile());
    }

    public static UserUpdateDTO toUpdateDTO(Users user) {
        UserUpdateDTO dto = new UserUpdateDTO();

        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setMobile(user.getMobile());

        return dto;
    }
}
