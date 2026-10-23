package com.example.onlineshopback.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.onlineshopback.model.WishlistItem;

public interface WishlistRepository extends JpaRepository<WishlistItem, Long>{
    
    @Query("""
        SELECT w FROM WishlistItem w
        JOIN FETCH w.product p
        LEFT JOIN FETCH p.images
        WHERE w.users.userId = :userId
        ORDER BY w.createdAt DESC
        """)
    List<WishlistItem> findByUserIdWithProduct(@Param("userId") Integer userId);

    Optional<WishlistItem> findByUsersUserIdAndProductProductId(Integer userId, Integer productId);

    boolean existsByUsersUserIdAndProductProductId(Integer userId, Integer productId);

    void deleteByUsersUserIdAndProductProductId(Integer userId, Integer productId);

    long countByUsersUserId(Integer userId);

}
