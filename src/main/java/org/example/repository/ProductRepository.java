package org.example.repository;

import jakarta.transaction.Transactional;
import org.example.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // Найти все товары, которые еще не были опубликованы
    List<Product> findByPostedFalse();

    // Обновить флаг posted для товара
    @Modifying
    @Transactional
    @Query("UPDATE Product p SET p.posted = true WHERE p.id = :id")
    void markAsPosted(Long id);
}
