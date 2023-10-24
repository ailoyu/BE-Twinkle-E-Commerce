package com.twinkle.shopapp.repositories;

import com.twinkle.shopapp.models.Category;
import com.twinkle.shopapp.models.Product;
import org.antlr.v4.runtime.misc.MultiMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsByName(String name);
    Page<Product> findAll(Pageable pageable);//phân trang


    // Đây là câu lệnh HQL
    @Query(value = "SELECT * FROM products p " +
            "WHERE " +
            "(:categoryId IS NULL OR :categoryId = 0 OR p.category_id = :categoryId) " +
            "AND " +
            "(:keyword IS NULL OR :keyword = '' OR p.name LIKE %:keyword%)", nativeQuery = true)
    Page<Product> searchProducts(@Param("categoryId") Long categoryId, @Param("keyword") String keyword, Pageable pageable);

//    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.productImages WHERE p.id = :productId")
//    Optional<Product> getDetailProduct(@Param("productId") Long productId);


    @Query("Select p from Product p where p.id in :productIds") // gửi ds productIds lấy ra ds sản phẩm
    List<Product> findProductById(@Param("productIds") List<Long> productIds);

    @Query(value = "SELECT p.* FROM order_details od " +
            "INNER JOIN orders o ON od.order_id = o.id " +
            "INNER JOIN products p ON od.product_id = p.id " +
            "WHERE o.status = 'Đã giao hàng' " +
            "ORDER BY od.number_of_products DESC " +
            "LIMIT 9", nativeQuery = true)
    List<Product> getAllBestSellers();

    @Query(value = "SELECT p.* FROM products p " +
            "INNER JOIN categories c ON p.category_id = c.id " +
            "WHERE p.category_id = :categoryId " +
            "ORDER BY RAND() " +
            "LIMIT 9", nativeQuery = true)
    List<Product> getProductsByCategory(@Param("categoryId") Long categoryId);

}
