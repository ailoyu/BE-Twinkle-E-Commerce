package com.twinkle.shopapp.repositories;

import com.twinkle.shopapp.models.Category;
import com.twinkle.shopapp.models.Order;
import com.twinkle.shopapp.models.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    //Tìm các đơn hàng của 1 user nào đó

    @Query(value = "SELECT * FROM orders WHERE orders.user_id = :userId ORDER by orders.order_date desc", nativeQuery = true)
    List<Order> findByUserId(Long userId);

    @Query(value = "SELECT * FROM orders WHERE orders.status = :orderStatus ORDER by orders.order_date desc", nativeQuery = true)
    List<Order> findAllByStatus(String orderStatus);
    
    @Query(value = "SELECT " +
            "    o.status, " +
            "    COUNT(o.id) AS so_lieu, " +
            "    (COUNT(o.id) / t.total_count) * 100 AS percentage " +
            "FROM orders o " +
            "JOIN ( " +
            "    SELECT COUNT(id) AS total_count " +
            "    FROM orders " +
            ") t " +
            "GROUP BY o.status, t.total_count;", nativeQuery = true)
    List<Object[]> getOrderStatusStatistics();

    
    @Query(value = "SELECT " +
            "    order_date, " +
            "    Round(SUM(tong_gia_goc), 2) AS sum_gia_goc, " +
            "    Round(SUM(tong_gia_loi_nhuan),2) AS sum_gia_loi_nhuan, " +
            "    Round(SUM(loi_nhuan), 2) AS sum_loi_nhuan, " +
            "    SUM(so_luong) AS sum_so_luong " +
            "FROM ( " +
            "    SELECT DISTINCT " +
            "        DATE(O.order_date) AS order_date, " +
            "        O.id AS order_id, " +
            "        O.fullname AS customer_name, " +
            "        O.phone_number AS customer_phone, " +
            "        DIO.price AS gia_goc, " +
            "        OD.product_price AS gia_loi_nhuan, " +
            "        OD.number_of_products AS so_luong, " +
            "        DIO.price * OD.number_of_products AS tong_gia_goc, " +
            "        OD.total_money AS tong_gia_loi_nhuan, " +
            "        (OD.total_money - DIO.price * OD.number_of_products) AS loi_nhuan " +
            "    FROM " +
            "        orders O " +
            "    JOIN " +
            "        order_details OD ON O.id = OD.order_id " +
            "    JOIN " +
            "        detail_input_order DIO ON OD.product_id = DIO.product_id " +
            "    WHERE O.`status` LIKE 'Đã giao hàng' " +
            ") AS subquery " +
            "GROUP BY order_date " +
            "ORDER BY order_date ASC; ", nativeQuery = true)
    List<Object[]> getRevenue();


}
