package com.twinkle.shopapp.controllers;

import com.twinkle.shopapp.dtos.DetailInputOrderDTO;
import com.twinkle.shopapp.models.DetailInputOrder;
import com.twinkle.shopapp.models.InputOrder;
import com.twinkle.shopapp.responses.DetailInputOrderResponse;
import com.twinkle.shopapp.responses.InputOrderResponse;
import com.twinkle.shopapp.services.IDetailInputOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${api.prefix}/detail_input_orders")
@RequiredArgsConstructor
public class DetailInputOrderController {

    private final IDetailInputOrderService detailInputOrderService;

    private ResponseEntity<DetailInputOrderResponse> handleResponse(DetailInputOrder detailInputOrder, String successMessage) {
        return ResponseEntity.ok(DetailInputOrderResponse.builder()
                .message(successMessage)
                .detailInputOrder(detailInputOrder)
                .build());
    }

    private ResponseEntity<DetailInputOrderResponse> handleErrorResponse(BindingResult result, String errorMessage) {
        List<String> errorMessages = result.getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getDefaultMessage())
                .collect(Collectors.toList());
        return ResponseEntity.badRequest().body(DetailInputOrderResponse.builder()
                .errors(errorMessages)
                .build());
    }

    @PostMapping("")
    public ResponseEntity<?> createDetailInputOrder(
            @Valid @RequestBody DetailInputOrderDTO detailInputOrderDTO,
            BindingResult result
            ){
        if (result.hasErrors()) {
            return handleErrorResponse(result, "Tạo detail input order thất bại");
        }
        try {
            DetailInputOrder inputOrder = detailInputOrderService.createDetailInputOrder(detailInputOrderDTO);
            return handleResponse(inputOrder, "Tạo input order thành công");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    DetailInputOrderResponse.builder()
                            .message(e.getMessage())
                            .build()
            );
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateDetailInputOrder(
            @PathVariable Long id,
            @Valid @RequestBody DetailInputOrderDTO detailInputOrderDTO,
            BindingResult result
    ){
        if (result.hasErrors()) {
            return handleErrorResponse(result, "Cập nhật detail input order thất bại");
        }
        try {
            DetailInputOrder inputOrder = detailInputOrderService.updateDetailInputOrder(id, detailInputOrderDTO);
            return handleResponse(inputOrder, "Cập nhật detail input order thành công");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    DetailInputOrderResponse.builder()
                            .message(e.getMessage())
                            .build()
            );
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findByInputOrder(
            @PathVariable Long id
    ) {
        List<DetailInputOrder> detailInputOrders = detailInputOrderService.findByInputOrder(id);
        return ResponseEntity.ok().body(detailInputOrders);
    }


    @DeleteMapping("")
    public ResponseEntity<?> deleteDetailInputOrder(@RequestBody Map<String, Long[]> request){
        try{
            Long[] ids = request.get("ids");
            detailInputOrderService.deleteDetailInputOrder(ids);
            return ResponseEntity.ok().body(InputOrderResponse.builder()
                    .message("Xóa thành công")
                    .build()
            );
        } catch (Exception e){
            return ResponseEntity.badRequest().body(InputOrderResponse.builder()
                    .message(e.getMessage())
                    .build()
            );
        }

    }



}
