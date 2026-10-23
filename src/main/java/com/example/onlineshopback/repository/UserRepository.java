package com.example.onlineshopback.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.onlineshopback.model.Users;

@Repository
public interface UserRepository extends JpaRepository<Users, Integer>{

    boolean existsByEmail(String email);

    boolean existsByMobile(String mobile);
    
    Users findByEmail(String email);

    

}
