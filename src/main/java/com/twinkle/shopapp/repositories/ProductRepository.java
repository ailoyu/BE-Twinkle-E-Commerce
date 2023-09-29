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
    @Query("SELECT p FROM Product p WHERE " +
            "(:categoryId IS NULL OR :categoryId = 0 OR p.category.id = :categoryId) " +
            "AND (:keyword IS NULL OR :keyword = '' OR p.name LIKE %:keyword% OR p.description LIKE %:keyword%)")
    Page<Product> searchProducts
            (@Param("categoryId") Long categoryId,
             @Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.productImages WHERE p.id = :productId")
    Optional<Product> getDetailProduct(@Param("productId") Long productId);


    @Query("Select p from Product p where p.id in :productIds") // gửi ds productIds lấy ra ds sản phẩm
    List<Product> findProductById(@Param("productIds") List<Long> productIds);

}
