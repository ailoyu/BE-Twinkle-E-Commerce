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

    @Query(value = "SELECT * FROM products p where p.is_active = 1", nativeQuery = true)
    Page<Product> findAll(Pageable pageable);//phân trang

    @Query(value = "SELECT COUNT(DISTINCT p.id) FROM products p " +
            "INNER JOIN detail_input_order d ON p.id = d.product_id " +
            "INNER JOIN input_order o on d.input_order_id = o.id " +
            "INNER JOIN provider pr on o.provider_id = pr.id " +
            "WHERE " +
            "(:categoryId IS NULL OR :categoryId = 0 OR p.category_id = :categoryId) " +
            "AND " +
            "(:keyword IS NULL OR :keyword = '' OR p.name LIKE %:keyword%) " +
            "AND " +
            "(:size IS NULL OR :size = 0.0 OR d.size = :size) " +
            "AND " +
            "(:selectedPriceRate IS NULL OR :selectedPriceRate = '' OR" +
            "(:selectedPriceRate = '< 50' AND d.price < 50) OR " +
            "(:selectedPriceRate = '>= 50 and <= 100' AND d.price >= 50 AND d.price <= 100) OR " +
            "(:selectedPriceRate = '> 100 and <= 200' AND d.price > 100 AND d.price <= 200) OR " +
            "(:selectedPriceRate = '> 200' AND d.price > 200)) " +
            "AND " +
            "(:selectedProvider IS NULL OR :selectedProvider = '' OR pr.name LIKE %:selectedProvider%) " +
            "AND " +
            "p.is_active = 1",
            nativeQuery = true)
    Long countDistinctProducts(@Param("categoryId") Long categoryId,
                               @Param("keyword") String keyword,
                               @Param("size") Float size,
                               @Param("selectedPriceRate") String selectedPriceRate,
                               @Param("selectedProvider") String selectedProvider);


    // Đây là câu lệnh HQL
    @Query(value = "SELECT DISTINCT p.id, p.name, p.description, p.thumbnail, p.is_active, p.category_id, p.created_at, p.updated_at, d.price FROM products p " +
            "INNER JOIN detail_input_order d ON p.id = d.product_id " +
            "INNER JOIN input_order o on d.input_order_id = o.id " +
            "INNER JOIN provider pr on o.provider_id = pr.id " +
            "WHERE " +
            "(:categoryId IS NULL OR :categoryId = 0 OR p.category_id = :categoryId) " +
            "AND " +
            "(:keyword IS NULL OR :keyword = '' OR p.name LIKE %:keyword%) " +
            "AND " +
            "(:size IS NULL OR :size = 0.0 OR d.size = :size) " +
            "AND " +
            "(:selectedPriceRate IS NULL OR :selectedPriceRate = '' OR" +
            "(:selectedPriceRate = '< 50' AND d.price < 50) OR " +
            "(:selectedPriceRate = '>= 50 and <= 100' AND d.price >= 50 AND d.price <= 100) OR " +
            "(:selectedPriceRate = '> 100 and <= 200' AND d.price > 100 AND d.price <= 200) OR " +
            "(:selectedPriceRate = '> 200' AND d.price > 200)) " +
            "AND " +
            "(:selectedProvider IS NULL OR :selectedProvider = '' OR pr.name LIKE %:selectedProvider%) " +
            "AND " +
            "p.is_active = 1 ",
            nativeQuery = true)
    Page<Product> searchProducts(@Param("categoryId") Long categoryId,
                                 @Param("keyword") String keyword,
                                 @Param("size") Float size,
                                 @Param("selectedPriceRate") String selectedPriceRate,
                                 @Param("selectedProvider") String selectedProvider,
                                 PageRequest pageRequest);

    @Query(value = "SELECT DISTINCT(d.size) FROM detail_input_order d " +
            "INNER JOIN products p on d.product_id = p.id " +
            "WHERE p.is_active = 1 " +
            "ORDER BY d.size asc"
            , nativeQuery = true)
    List<Float> getAllAvailableSizes();


    @Query(value = "Select * from products p where p.id in :productIds AND p.is_active = 1", nativeQuery = true) // gửi ds productIds lấy ra ds sản phẩm
    List<Product> findActiveProductsByIdIn(@Param("productIds") List<Long> productIds);

    @Query(value = "SELECT p.name, p.id, p.thumbnail, p.description, p.is_active, p.updated_at, p.created_at, p.category_id " +
            "FROM detail_input_order d " +
            "INNER JOIN products p ON d.product_id = p.id " +
            "INNER JOIN input_order i ON d.input_order_id = i.id " +
            "INNER JOIN provider pr ON i.provider_id = pr.id " +
            "WHERE pr.name LIKE %?1% AND p.is_active = 1 " +
            "GROUP BY p.id ", nativeQuery = true)
    List<Product> findProductsByBrand(@Param("brand") String brand);


    @Query(value = "SELECT p.* " +
            "FROM products p ORDER by p.created_at DESC LIMIT 9;", nativeQuery = true)
    List<Product> getNewProducts();

    @Query(value = "SELECT DISTINCT p.* " +
            "FROM (SELECT p.* FROM order_details od " +
            "INNER JOIN orders o ON od.order_id = o.id " +
            "INNER JOIN products p ON od.product_id = p.id " +
            "WHERE o.status = 'Đã giao hàng' AND p.is_active = 1 " +
            "ORDER BY od.number_of_products DESC " +
            ") p LIMIT 4", nativeQuery = true)
    List<Product> getAllBestSellers();

    @Query(value = "SELECT p.* FROM products p " +
            "INNER JOIN categories c ON p.category_id = c.id " +
            "WHERE p.category_id = :categoryId AND p.is_active = 1 " +
            "ORDER BY RAND() " +
            "LIMIT 9", nativeQuery = true)
    List<Product> getProductsByCategory(@Param("categoryId") Long categoryId);

    Optional<Product> findByIdAndActive(long id, boolean isActive);
}
