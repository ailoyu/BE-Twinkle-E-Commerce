package com.twinkle.shopapp.services.impl;

import com.twinkle.shopapp.dtos.CartItemDTO;
import com.twinkle.shopapp.dtos.OrderDTO;
import com.twinkle.shopapp.exceptions.DataNotFoundException;
import com.twinkle.shopapp.models.*;
import com.twinkle.shopapp.repositories.OrderDetailRepository;
import com.twinkle.shopapp.repositories.OrderRepository;
import com.twinkle.shopapp.repositories.ProductRepository;
import com.twinkle.shopapp.repositories.UserRepository;
import com.twinkle.shopapp.responses.OrderResponse;
import com.twinkle.shopapp.services.IOrderService;
import com.twinkle.shopapp.utils.EmailUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService {

    private final UserRepository userRepository;

    private final OrderRepository orderRepository;

    private final ModelMapper modelMapper;

    private final ProductRepository productRepository;

    private final OrderDetailRepository orderDetailRepository;

    @Override
    @Transactional // rollback dữ liệu khi bị sai gì đó
    public Order createOrder(OrderDTO orderDTO) throws Exception {
        // tìm xem user's id đã tồn tại chưa
        User user = userRepository.findById(orderDTO.getUserId())
                .orElseThrow(() -> new DataNotFoundException("Ko tìm thấy User với id " + orderDTO.getUserId()));

        Order order = new Order();
        // Dùng Model Mapper

        // convert DTO -> Order (Nhưng ko mapping id)
        // Cài đặt ánh xạ (ko ánh xạ id của order)
        modelMapper.typeMap(OrderDTO.class, Order.class)
                .addMappings(mapper -> mapper.skip(Order::setId));

        // Bắt đầu ánh xạ (từ orderDTO -> order)
        modelMapper.map(orderDTO, order);
        order.setUser(user); // user đặt hàng
        order.setOrderDate(new Date()); // ngày đặt hàng là ngày hiện tại
        order.setStatus(OrderStatus.PENDING); // 1 đơn hàng vừa tạo ra trạng thái là PENDING

        // kIỂM TRA nếu khách hàng k nhập shipping date, lấy luôn ngày hnay
        LocalDate shippingDate = orderDTO.getShippingDate() == null
                ? LocalDate.now().plusDays(3) : orderDTO.getShippingDate();

        //shippingDate phải >= ngày hôm nay
        if(shippingDate.isBefore(LocalDate.now())){
            throw new DataNotFoundException("Ngày giao hàng phải lớn hơn ngày hôm nay");
        }

        order.setShippingDate(shippingDate);
        order.setActive(true);
        order.setTotalMoney(orderDTO.getTotalMoney());

        List<OrderDetail> orderDetails = new ArrayList<>();
        for(CartItemDTO cartItemDTO : orderDTO.getCartItems()){

            // Bỏ order vào từng order detail
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);

            // lấy ra từng sản phẩm và số lượng vào trong giỏ hàng
            Long productId = cartItemDTO.getProductId();
            Integer quantity = cartItemDTO.getQuantity();

            // Tìm thông tin từng product này có trong DB hay ko? (or sử dụng cache neu cần)
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new DataNotFoundException("Ko tìm thấy sản phẩm"));

            // set sản phẩm và số lượng vào trong giỏ hàng
            orderDetail.setProduct(product);
            orderDetail.setNumberOfProducts(quantity);


            // set giá cho từng sản phẩm
            orderDetail.setPrice(product.getPrice());

            // Set tổng tiền
            orderDetail.setTotalMoney(product.getPrice() * quantity);

            // thêm orderDetail vào danh sách
            orderDetails.add(orderDetail);
        }

        // Lưu danh sách orderDetail vào DB
        List<OrderDetail> listOrder = orderDetailRepository.saveAll(orderDetails);

        if(listOrder != null)
            orderRepository.save(order);

        /// Gửi email sau khi order
        String emailContent = EmailUtils.getEmailContent(order, listOrder);

        String[] recipients = {order.getEmail(), "quangtrinhhuynh02@gmail.com"};

        EmailUtils.sendEmail(recipients, "Đá núi lửa Hồng Quang | Chúc mừng! Đơn hàng của bạn đã được đặt thành công và đang xử lý!!", emailContent);

        return order;
    }

    @Override
    public Order getOrder(Long id) throws DataNotFoundException {
        return orderRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Ko tìm thấy đơn hàng này!"));
    }

    @Override
    @Transactional // rollback dữ liệu khi bị sai gì đó
    public Order updateOrder(Long id, String status) throws DataNotFoundException {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Ko tìm thấy order này để update"));

        order.setStatus(status);

        return orderRepository.save(order);
    }

    @Override
    @Transactional // rollback dữ liệu khi bị sai gì đó
    public void deleteOrder(Long[] ids) throws DataNotFoundException {
        for(long id : ids){
            Optional<Order> optionalorder = orderRepository.findById(id);
            if(optionalorder.isPresent()){
                orderRepository.delete(optionalorder.get()); // nếu có product trong DB ms xóa

            }
        }
    }

    @Override
    public List<Order> findByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    @Override
    public List<Order> getPendingOrders() {
        return orderRepository.findAllByStatus(OrderStatus.PENDING);
    }

    @Override
    public List<Order> getShippingOrders() {
        return orderRepository.findAllByStatus(OrderStatus.SHIPPING);
    }

    @Override
    public List<Order> getDeliveredOrders() {
        return orderRepository.findAllByStatus(OrderStatus.DELIVERED);
    }

    @Override
    public List<Order> getCancelledOrders() {
        return orderRepository.findAllByStatus(OrderStatus.CANCELLED);
    }
}
