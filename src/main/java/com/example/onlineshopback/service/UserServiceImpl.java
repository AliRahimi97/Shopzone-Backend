package com.example.onlineshopback.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.onlineshopback.dto.UserListItemDTO;
import com.example.onlineshopback.dto.UserUpdateDTO;
import com.example.onlineshopback.dto.request.AdminRegisterDTO;
import com.example.onlineshopback.dto.request.CustomerRegisterDTO;
import com.example.onlineshopback.dto.request.LoginRequestDTO;
import com.example.onlineshopback.dto.response.AdminResponseDTO;
import com.example.onlineshopback.dto.response.CustomerResponseDTO;
import com.example.onlineshopback.dto.response.LoginResponseDTO;
import com.example.onlineshopback.mapper.UserMapper;
import com.example.onlineshopback.model.Role;
import com.example.onlineshopback.model.Users;
import com.example.onlineshopback.repository.UserRepository;
import com.example.onlineshopback.security.JwtUtils;

import jakarta.servlet.http.HttpServletRequest;
 



@Service
public class UserServiceImpl implements UserService{


    @Autowired
    private UserRepository userRepository;

    @Value("${app.admin.code}")
    private String adminCode;

    @Autowired
    private JwtUtils jwtUtil;

    @Override
    public CustomerResponseDTO registerCustomer(CustomerRegisterDTO customerDTO) {
        
        if (userRepository.existsByEmail(customerDTO.getEmail())) {
            throw new RuntimeException("This email is already registered");
        }

        if(userRepository.existsByMobile(customerDTO.getMobile())){
            throw new RuntimeException("This mobile number is already registered");
        }

        Users newUser = new Users();
        newUser.setUserId(customerDTO.getUserId());
        newUser.setFirstName(customerDTO.getFirstName());
        newUser.setLastName(customerDTO.getLastName());
        newUser.setEmail(customerDTO.getEmail());
        newUser.setMobile(customerDTO.getMobile());
        newUser.setPassword(customerDTO.getPassword());
        newUser.setCreatedOn(LocalDateTime.now());
        newUser.setRole(Role.CUSTOMER);

        Users savedUser = userRepository.save(newUser);

        CustomerResponseDTO response = UserMapper.toCustomerResponseDTO(savedUser);

        return response;
    }

    @Override
    public AdminResponseDTO registerAdmin(AdminRegisterDTO adminDTO) {
        if (userRepository.existsByEmail(adminDTO.getEmail())) {
            throw new RuntimeException("This admin email is already exist");
        }
        if (userRepository.existsByMobile(adminDTO.getMobile())) {
            throw new RuntimeException("This admin mobile number is already exist.");
        }

        System.out.println("Root Admin Code : " + adminCode);
        System.out.println("Admin Code " + adminDTO.getAdminCode());
        if (!(adminDTO.getAdminCode().trim().equals(adminCode.trim()))) {
            throw new RuntimeException("Invalid admin code");
        }

        Users admin = new Users();
        admin.setFirstName(adminDTO.getFirstName());
        admin.setLastName(adminDTO.getLastName());
        admin.setEmail(adminDTO.getEmail());
        admin.setMobile(adminDTO.getMobile());
        admin.setPassword(adminDTO.getPassword());
        admin.setRole(Role.ADMIN);
        admin.setCreatedOn(LocalDateTime.now());

        Users saveAdmin = userRepository.save(admin);

        AdminResponseDTO reposnse = UserMapper.toAdminResponseDTO(saveAdmin);

        return reposnse;
    }

    @Override
    public LoginResponseDTO loginUser(LoginRequestDTO loginDTO) {
        Users user = userRepository.findByEmail(loginDTO.getEmail());
        
        if(user == null || !user.getPassword().equals(loginDTO.getPassword())) 
            throw new RuntimeException("Invalid email or password");

        String token = jwtUtil.generateToken(user);

        LoginResponseDTO response = new LoginResponseDTO();
        response.setMessage("Login successful");
        response.setToken(token);
        
        if (user.getRole() == Role.CUSTOMER) {
            CustomerResponseDTO customerDto = UserMapper.toCustomerResponseDTO(user);
            response.setCustomer(customerDto);
        } else if (user.getRole() == Role.ADMIN) {
            AdminResponseDTO adminDto = UserMapper.toAdminResponseDTO(user);
            response.setAdmin(adminDto);
        }

        return response;
    }

    @Override
    public Object getUserProfile(HttpServletRequest request) {

        String authHeader = request.getHeader("Authorization");
        if(authHeader == null || !authHeader.startsWith("Bearer")) {
            throw new RuntimeException("Token is missing");
        }
        String token = authHeader.substring(7);
        Integer userId = jwtUtil.extractUserId(token);

        if (userId == null) {
            throw new RuntimeException("Invalid token");
        }
        Users user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("user not found"));

            if (!user.getUserId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied!");
            }

            
        if (user.getRole() == Role.CUSTOMER) {

            return UserMapper.toCustomerResponseDTO(user);
        } else {
            return UserMapper.toAdminResponseDTO(user);
        }
    }

    @Override
    public CustomerResponseDTO updateCustomer(Integer userId, UserUpdateDTO updateDTO) {
        Users user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("user not found!"));

        if (user.getRole() != Role.CUSTOMER) {
            throw new RuntimeException("not a customer account !");
        }

        user.setFirstName(updateDTO.getFirstName());
        user.setLastName(updateDTO.getLastName());
        user.setEmail(updateDTO.getEmail());
        user.setMobile(updateDTO.getMobile());


        Users updateUser = userRepository.save(user);

        return UserMapper.toCustomerResponseDTO(updateUser);
        
    }

    @Override
    public AdminResponseDTO updateAdmin(Integer userId, UserUpdateDTO updateDTO) {
        Users user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found !"));

        if (user.getRole() != Role.ADMIN) {
            throw new RuntimeException("Not an Admin account");
        }

        user.setFirstName(updateDTO.getFirstName());
        user.setLastName(updateDTO.getLastName());
        user.setEmail(updateDTO.getEmail());
        user.setMobile(updateDTO.getMobile());

        Users updateAdmin = userRepository.save(user);
        return UserMapper.toAdminResponseDTO(updateAdmin);

    }

    @Override
    public List<UserListItemDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(u -> UserListItemDTO.builder()
                        .userId(u.getUserId())
                        .firstName(u.getFirstName())
                        .lastName(u.getLastName())
                        .email(u.getEmail())
                        .mobile(u.getMobile())
                        .role(u.getRole() != null ? u.getRole().name() : null)
                        .createdOn(u.getCreatedOn() != null ? u.getCreatedOn().toString() : null)
                        .build())
                .toList();
    }

    @Override
    public UserListItemDTO getUserById(Integer userId) {
        Users u = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return UserListItemDTO.builder()
                .userId(u.getUserId())
                .firstName(u.getFirstName())
                .lastName(u.getLastName())
                .email(u.getEmail())
                .mobile(u.getMobile())
                .role(u.getRole() != null ? u.getRole().name() : null)
                .createdOn(u.getCreatedOn() != null ? u.getCreatedOn().toString() : null)
                .build();
    }    
    
}
