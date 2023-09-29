package com.twinkle.shopapp.controllers;


import com.twinkle.shopapp.component.LocalizationUtils;
import com.twinkle.shopapp.dtos.OrderDetailDTO;
import com.twinkle.shopapp.exceptions.DataNotFoundException;
import com.twinkle.shopapp.models.OrderDetail;
import com.twinkle.shopapp.responses.OrderDetailResponse;
import com.twinkle.shopapp.services.IOrderDetailService;
import com.twinkle.shopapp.utils.MessageKeys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/order_details")
@RequiredArgsConstructor
public class OrderDetailController {

    private final IOrderDetailService orderDetailService;

    private final LocalizationUtils localizationUtils;


    @PostMapping("")
    public ResponseEntity<?> createOrderDetail(
            @Valid @RequestBody OrderDetailDTO orderDetailDTO
            ){
        try{
            OrderDetail orderDetail = orderDetailService.createOrderDetail(orderDetailDTO);
            return ResponseEntity.ok(OrderDetailResponse.fromOrderDetail(orderDetail));
//            return ResponseEntity.ok(orderDetail);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderDetail(
            @Valid @PathVariable Long id
    ){
        try{
            OrderDetail orderDetail = orderDetailService.getOrderDetail(id);
            return ResponseEntity.ok(OrderDetailResponse.fromOrderDetail(orderDetail));
//            return ResponseEntity.ok(orderDetail);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Lấy ra dánh sách các order_details của 1 order nào đó
    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getOrderDetails(
            @Valid @PathVariable Long orderId){

        List<OrderDetail> orderDetails = orderDetailService.findByOrderId(orderId);

        List<OrderDetailResponse> orderDetailResponse = orderDetails
                        .stream()
                        .map(OrderDetailResponse::fromOrderDetail)
                        .toList();
        return ResponseEntity.ok(orderDetailResponse);
    }


    @PutMapping ("/{id}")
    public ResponseEntity<?> updateOrderDetail(
            @Valid @RequestBody OrderDetailDTO orderDetailDTO,
            @PathVariable Long id
    ) {
        try{
            OrderDetail orderDetail = orderDetailService.updateOrderDetail(id, orderDetailDTO);
            return ResponseEntity.ok(orderDetail);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrderDetail(
            @Valid @RequestBody OrderDetailDTO orderDetailDTO,
            @PathVariable Long id
    ){
        orderDetailService.deleteById(id);
        return ResponseEntity.ok()
                .body(localizationUtils
                        .getLocalizedMessage(MessageKeys.DELETE_ORDER_DETAIL_SUCCESSFULLY));
    }

}
