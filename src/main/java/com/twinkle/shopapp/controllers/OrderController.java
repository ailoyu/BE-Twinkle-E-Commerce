package com.twinkle.shopapp.controllers;


import com.twinkle.shopapp.component.LocalizationUtils;
import com.twinkle.shopapp.dtos.OrderDTO;
import com.twinkle.shopapp.models.Order;
import com.twinkle.shopapp.responses.CategoryResponse;
import com.twinkle.shopapp.responses.OrderResponse;
import com.twinkle.shopapp.services.IOrderService;
import com.twinkle.shopapp.utils.MessageKeys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${api.prefix}/orders")
@RequiredArgsConstructor
public class OrderController {

    private final IOrderService orderService;

    private final LocalizationUtils localizationUtils;


    @PostMapping("")
    public ResponseEntity<?> createOrder(
            @RequestBody @Valid OrderDTO orderDTO,
            BindingResult result){
        try {
            if(result.hasErrors()){
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(fieldError -> fieldError.getDefaultMessage())
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            String paymenURL = orderService.createOrder(orderDTO);
            System.out.println(paymenURL);
            return ResponseEntity.ok().body(OrderResponse.builder()
                    .paymentMethod(paymenURL)
                    .build());
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }



    // Cập nhật trạng thái hoặc tổng tiền (của admin)
    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrder(
            @PathVariable Long id,
            @RequestBody String status){
        try{
            Order order = orderService.updateOrder(id, status);
            return ResponseEntity.ok(order);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @DeleteMapping("/")
    public ResponseEntity<?> deleteOrder(@RequestBody Map<String, Long[]> request){
        // Xóa mềm => Cập nhật active = false;
        try{
            Long[] ids = request.get("ids");
            orderService.deleteOrder(ids);
            return ResponseEntity.ok().body(CategoryResponse.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.DELETE_ORDER_SUCCESSFULLY))
                    .build());
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }



    @GetMapping("/history/{user_id}")
    public ResponseEntity<?> getOrdersByUserId(@Valid @PathVariable("user_id") Long userId){
        try {
            // Hiện ra dánh sách các order của khách hàng đã mua
            List<Order> orders = orderService.findByUserId(userId);

            return ResponseEntity.status(HttpStatus.OK).body(orders);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    @GetMapping("/order-confirm/pending")
    public ResponseEntity<?> getPendingOrders(){
        return ResponseEntity.status(HttpStatus.OK).body(orderService.getPendingOrders());
    }

    @GetMapping("/order-confirm/shipping")
    public ResponseEntity<?> getShippingOrders(){
        return ResponseEntity.status(HttpStatus.OK).body(orderService.getShippingOrders());
    }

    @GetMapping("/order-confirm/delivered")
    public ResponseEntity<?> getDeliveredOrders(){
        return ResponseEntity.status(HttpStatus.OK).body(orderService.getDeliveredOrders());
    }

    @GetMapping("/order-confirm/cancelled")
    public ResponseEntity<?> getCancelledOrders(){
        return ResponseEntity.status(HttpStatus.OK).body(orderService.getCancelledOrders());
    }



    @GetMapping("/{id}")
    public ResponseEntity<?> getOrder(@Valid @PathVariable("id") Long orderId){
        try {
            Order existingOrder = orderService.getOrder(orderId);
            return ResponseEntity.status(HttpStatus.OK).body(existingOrder);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


}
