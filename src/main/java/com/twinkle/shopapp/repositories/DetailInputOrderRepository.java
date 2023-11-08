package com.twinkle.shopapp.repositories;

import com.twinkle.shopapp.models.DetailInputOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DetailInputOrderRepository extends JpaRepository<DetailInputOrder, Long> {
    List<DetailInputOrder> findByInputOrderId(Long id);


    List<DetailInputOrder> findByProductId(Long id);
    
    @Query(value = "SELECT " +
            "    DIO.size AS kich_thuoc, " +
            "    SUM(DIO.quantity) AS so_luong, " +
            "    (SUM(DIO.quantity) / (SELECT SUM(quantity) FROM detail_input_order)) * 100 AS phan_tram " +
            "FROM " +
            "    detail_input_order DIO " +
            "GROUP BY " +
            "    DIO.size;", nativeQuery = true)
    List<Object[]> getSizesStatistic();
    
    @Query(value = "SELECT   " +
            "    CASE   " +
            "        WHEN DIO.price < 50 THEN 'under $50'   " +
            "        WHEN DIO.price >= 50 AND DIO.price < 100 THEN '$50 - $100'   " +
            "        WHEN DIO.price >= 100 AND DIO.price < 200 THEN '$100 - $200'   " +
            "        ELSE 'over $200'   " +
            "    END AS gia_range,   " +
            "    SUM(DIO.quantity) AS so_luong,   " +
            "    (SUM(DIO.quantity) / (SELECT SUM(quantity) FROM detail_input_order)) * 100 AS phan_tram   " +
            "FROM   " +
            "    detail_input_order DIO   " +
            "GROUP BY   " +
            "    gia_range;", nativeQuery = true)
    List<Object[]> getPriceStatistics();

}
