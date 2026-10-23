package com.example.onlineshopback.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.onlineshopback.model.CategoryEnum;
import com.example.onlineshopback.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    List<Product> findByCategory(CategoryEnum category);

    @Query("SELECT p FROM Product p WHERE p.price > :minPrice")
    List<Product> getDiscountProducts(@Param("minPrice") Double minPrice);

    @Query("SELECT p FROM Product p WHERE LOWER(p.brand) = LOWER(:brand)")
    List<Product> findByBrandIgnoreCase(@Param("brand") String brand);

    List<Product> findByStockLessThan(Integer stock);

    // ========== متد جدید برای لود کردن عکس‌ها ==========
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.images WHERE p.productId = :id")
    Optional<Product> findByIdWithImages(@Param("id") Integer id);

    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.images")
    List<Product> findAllWithImages();

    @Query("""
        SELECT DISTINCT p FROM Product p
        LEFT JOIN FETCH p.images
        WHERE p.active = true
        AND (
            LOWER(p.name) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(p.brand) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(COALESCE(p.description, '')) LIKE LOWER(CONCAT('%', :q, '%'))
        )
        ORDER BY p.name
        """)
    List<Product> searchActive(@Param("q") String q);
}