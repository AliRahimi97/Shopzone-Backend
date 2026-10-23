package com.example.onlineshopback.model;

import java.time.LocalDateTime;
import java.util.ArrayList; 
import java.util.List; 

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column; 
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class Users {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer userId;

    @NotNull 
    @Column(name = "first_name")
    @Pattern(regexp = "[A-Za-z.\\s]+", message = "Enter valid first name")
    private String firstName;

    @NotNull
    @Column(name = "last_name")
    @Pattern(regexp = "[A-Za-z.\\s]+", message = "Enter valid last name")
    private String lastName;

   
    @Column(name = "mobile_number", unique = true)
    @Pattern(regexp = "[6789]{1}[0-9]{9}", message = "Enter a valid Mobile Number")
    private String mobile;

    @Column(name = "email", unique = true)
    @Email
    private String email;
    
    @NotNull
    @Column(name = "password")
    
    private String password;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role; //Admin or Customer

    @Column(name = "created_on")
    private LocalDateTime createdOn;

    //only for customer 
    //addresses
    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL)
    private List<Address> address = new ArrayList<>();

    //orders
    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL)
    private List<OrderShop> orders = new ArrayList<>();

    //cart items 
    @OneToMany(mappedBy = "users" , cascade = CascadeType.ALL)
    private List<CartItem> cartItems = new ArrayList<>();
    
    //only for Admins 
    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL)
@JsonIgnore
    private List<Product> products = new ArrayList<>();


    
    
}
