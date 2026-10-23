package com.example.onlineshopback.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.onlineshopback.model.OrderShop;
import com.example.onlineshopback.model.OrderStatus;

@Repository
public interface OrderRepository extends JpaRepository<OrderShop, Long> {

    List<OrderShop> findByUsersUserIdOrderByOrderDateDesc(Integer userId);

    @Query("""
        SELECT o FROM OrderShop o
        LEFT JOIN FETCH o.orderItems oi
        LEFT JOIN FETCH oi.product
        WHERE o.orderId = :orderId
        """)
    Optional<OrderShop> findByIdWithItems(@Param("orderId") Long orderId);

    @Query("""
        SELECT DISTINCT o FROM OrderShop o
        LEFT JOIN FETCH o.orderItems oi
        LEFT JOIN FETCH oi.product
        WHERE o.users.userId = :userId
        ORDER BY o.orderDate DESC
        """)
    List<OrderShop> findByUserIdWithItems(@Param("userId") Integer userId);

    @Query("""
    SELECT DISTINCT o FROM OrderShop o
    LEFT JOIN FETCH o.orderItems oi
    LEFT JOIN FETCH oi.product
    ORDER BY o.orderDate DESC
    """)
    List<OrderShop> findAllWithItems();

    @Query("""
    SELECT DISTINCT o FROM OrderShop o
    LEFT JOIN FETCH o.orderItems oi
    LEFT JOIN FETCH oi.product
    WHERE o.status = :status
    ORDER BY o.orderDate DESC
    """)
    List<OrderShop> findByStatusWithItems(@Param("status") OrderStatus status);
}