package com.twinkle.shopapp.repositories;

import com.twinkle.shopapp.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query(value = "SELECT c.name AS category_name, COUNT(p.id) AS total_products, " +
            "ROUND(COUNT(p.id) / (SELECT COUNT(*) FROM products) * 100, 2) AS percentage_of_products " +
            "FROM categories c " +
            "LEFT JOIN products p ON c.id = p.category_id " +
            "GROUP BY c.id " +
            "ORDER BY percentage_of_products DESC", nativeQuery = true)
    List<Object[]> getCategoryStatistics();
}
