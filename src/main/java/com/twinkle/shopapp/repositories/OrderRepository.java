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
}
