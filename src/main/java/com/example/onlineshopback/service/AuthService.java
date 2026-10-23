package com.example.onlineshopback.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.onlineshopback.dto.request.AdminRegisterDTO;
import com.example.onlineshopback.dto.request.CustomerRegisterDTO;
import com.example.onlineshopback.dto.request.LoginRequestDTO;
import com.example.onlineshopback.dto.response.LoginResponseDTO;
import com.example.onlineshopback.mapper.UserMapper;
import com.example.onlineshopback.model.Role;
import com.example.onlineshopback.model.Users;
import com.example.onlineshopback.repository.UserRepository;
import com.example.onlineshopback.security.JwtUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.code}")
    private String adminCode;

    public LoginResponseDTO authenticate(LoginRequestDTO loginRequestDTO){
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequestDTO.getEmail(), loginRequestDTO.getPassword())
            );

            Users users = userRepository.findByEmail(loginRequestDTO.getEmail());

            String token = jwtUtil.generateToken(users);

            LoginResponseDTO response = new LoginResponseDTO();
            response.setMessage("Login Successful");
            response.setToken(token);

            if (users.getRole() == Role.ADMIN) {
                response.setAdmin(UserMapper.toAdminResponseDTO(users));

            }else if(users.getRole() == Role.CUSTOMER) {
                response.setCustomer(UserMapper.toCustomerResponseDTO(users));
            }
            return response;
        }catch (BadCredentialsException ex) {
            return new LoginResponseDTO("Invalid credentials", null, null, null);
        }
    }

    public LoginResponseDTO registerCustomer(CustomerRegisterDTO customerRegisterDTO) {
        if(userRepository.existsByEmail(customerRegisterDTO.getEmail())) {
            throw new RuntimeException("Email is already in use");
        }

        if (userRepository.existsByMobile(customerRegisterDTO.getMobile())) {
            throw new RuntimeException("This admin mobile number is already exist.");
        }

        Users user = UserMapper.toEntityFromCustomerRegister(customerRegisterDTO);

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Users savedUser = userRepository.save(user);

        String token = jwtUtil.generateToken(savedUser);

        LoginResponseDTO response = new LoginResponseDTO();
        response.setMessage("Customer register successful");
        response.setToken(token);

        response.setCustomer(UserMapper.toCustomerResponseDTO(savedUser));

        return response;

    }

    public LoginResponseDTO registerAdmin(AdminRegisterDTO registerDTO) {
        if(userRepository.existsByEmail(registerDTO.getEmail())) {
            throw new RuntimeException("Email is already in use");
        }
        if (userRepository.existsByMobile(registerDTO.getMobile())) {
            throw new RuntimeException("This admin mobile number is already exist.");
        }

        if (!(registerDTO.getAdminCode().trim().equals(adminCode.trim()))) {
            throw new RuntimeException("Invalid admin code");
        }   

        Users user = UserMapper.toEntityFromAdminRegister(registerDTO);

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Users savedAdmin = userRepository.save(user);

        String token = jwtUtil.generateToken(user);

        LoginResponseDTO respone = new LoginResponseDTO();

        respone.setMessage("Admin registered Successful");
        respone.setToken(token);
        respone.setAdmin(UserMapper.toAdminResponseDTO(savedAdmin));

        return respone;
    }

}
