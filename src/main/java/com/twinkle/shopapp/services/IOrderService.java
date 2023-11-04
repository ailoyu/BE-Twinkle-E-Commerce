package com.twinkle.shopapp.services;

import com.twinkle.shopapp.dtos.OrderDTO;
import com.twinkle.shopapp.exceptions.DataNotFoundException;
import com.twinkle.shopapp.models.Order;

import java.util.List;

public interface IOrderService {
    String createOrder(OrderDTO orderDTO) throws Exception;
    Order getOrder(Long id) throws DataNotFoundException;
    Order updateOrder(Long id, String status) throws DataNotFoundException;
    void deleteOrder(Long[] ids) throws DataNotFoundException;
    List<Order> findByUserId(Long userId);

    List<Order> getPendingOrders();

    List<Order> getShippingOrders();

    List<Order> getDeliveredOrders();

    List<Order> getCancelledOrders();



}
