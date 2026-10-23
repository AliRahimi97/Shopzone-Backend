package com.example.onlineshopback.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.onlineshopback.model.CartItem;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {

    @Query("""
        SELECT c FROM CartItem c
        JOIN FETCH c.product p
        WHERE c.users.userId = :userId
        """)
    List<CartItem> findByUserIdWithProduct(@Param("userId") Integer userId);

    Optional<CartItem> findByUsersUserIdAndProductProductId(Integer userId, Integer productId);

    Optional<CartItem> findByCartItemIdAndUsersUserId(Integer cartItemId, Integer userId);

    void deleteByUsersUserId(Integer userId);
}