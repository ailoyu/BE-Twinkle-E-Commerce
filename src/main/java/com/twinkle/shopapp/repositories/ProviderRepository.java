package com.twinkle.shopapp.repositories;

import com.twinkle.shopapp.models.Provider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProviderRepository extends JpaRepository<Provider, Long> {

    @Query(value = "SELECT * FROM provider p " +
            "WHERE " +
            "(:address IS NULL OR :address = '0' OR p.address LIKE %:address%) " +
            "AND " +
            "(:keyword IS NULL OR :keyword = '' OR p.name LIKE %:keyword%)", nativeQuery = true)
    Page<Provider> searchProviders(String keyword, String address, PageRequest pageRequest);

    @Query(value = "WITH BrandTotalQuantity AS ( " +
            "    SELECT " +
            "        p.id AS brand_id, " +
            "        SUM(dio.quantity) AS total_quantity " +
            "    FROM " +
            "        provider p " +
            "    LEFT JOIN " +
            "        input_order io ON p.id = io.provider_id " +
            "    LEFT JOIN " +
            "        detail_input_order dio ON io.id = dio.input_order_id " +
            "    GROUP BY " +
            "        p.id " +
            ") " +
            " " +
            "SELECT " +
            "    p.name AS brand_name, " +
            "    B.total_quantity AS total_quantity, " +
            "    ROUND((B.total_quantity / T.total_total_quantity) * 100, 2) AS percentage_of_total " +
            "FROM " +
            "    provider p " +
            "LEFT JOIN " +
            "    BrandTotalQuantity B ON p.id = B.brand_id " +
            "CROSS JOIN " +
            "    (SELECT SUM(total_quantity) AS total_total_quantity FROM BrandTotalQuantity) T " +
            "ORDER BY " +
            "    total_quantity DESC;", nativeQuery = true)
    List<Object[]> getProviderStatistics();
    
    @Query(value = "SELECT  " +
            "    pr.name AS provider_name,  " +
            "    p.name AS product_name,  " +
            "    SUM(dio.quantity) AS quantity_of_each_product  " +
            "FROM  " +
            "    provider pr  " +
            "INNER JOIN  " +
            "    input_order io ON pr.id = io.provider_id  " +
            "INNER JOIN  " +
            "    detail_input_order dio ON io.id = dio.input_order_id  " +
            "INNER JOIN  " +
            "    products p ON dio.product_id = p.id  " +
            "GROUP BY  " +
            "    pr.name, p.name  ", nativeQuery = true)
    List<Object[]> getQuantityOfEachProductStatistics();
}
