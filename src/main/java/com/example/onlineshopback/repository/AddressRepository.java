package com.example.onlineshopback.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.onlineshopback.model.Address;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long>{
    
}
