package com.twinkle.shopapp.controllers;

import com.twinkle.shopapp.dtos.InputOrderDTO;
import com.twinkle.shopapp.models.InputOrder;
import com.twinkle.shopapp.responses.InputOrderResponse;
import com.twinkle.shopapp.services.IInputOrderService;
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
@RequestMapping("${api.prefix}/input_orders")
@RequiredArgsConstructor
public class InputOrderController {

    private final IInputOrderService inputOrderService;


    private ResponseEntity<InputOrderResponse> handleResponse(InputOrder inputOrder, String successMessage) {
        return ResponseEntity.ok(InputOrderResponse.builder()
                .message(successMessage)
                .inputOrder(inputOrder)
                .build());
    }

    private ResponseEntity<InputOrderResponse> handleErrorResponse(BindingResult result, String errorMessage) {
        List<String> errorMessages = result.getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getDefaultMessage())
                .collect(Collectors.toList());
        return ResponseEntity.badRequest().body(InputOrderResponse.builder()
                .errors(errorMessages)
                .build());
    }

    @PostMapping("")
    public ResponseEntity<InputOrderResponse> createInputOrder(
            @Valid @RequestBody InputOrderDTO inputOrderDTO,
            BindingResult result
    ) {
        if (result.hasErrors()) {
            return handleErrorResponse(result, "Tạo input order thất bại");
        }
        try {
            InputOrder inputOrder = inputOrderService.createInputOrder(inputOrderDTO);
            return handleResponse(inputOrder, "Tạo input order thành công");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    InputOrderResponse.builder()
                            .message(e.getMessage())
                            .build()
            );
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<InputOrderResponse> updateInputOrder(
            @PathVariable Long id,
            @Valid @RequestBody InputOrderDTO inputOrderDTO,
            BindingResult result
    ) {
        if (result.hasErrors()) {
            return handleErrorResponse(result, "Cập nhật input order thất bại");
        }
        try {
            InputOrder inputOrder = inputOrderService.updateInputOrder(id, inputOrderDTO);
            return handleResponse(inputOrder, "Cập nhật input order thành công");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    InputOrderResponse.builder()
                            .message(e.getMessage())
                            .build()
            );
        }
    }


    @GetMapping("/provider/{id}")
    public ResponseEntity<?> findByProvider(
            @PathVariable Long id
    ) {
        List<InputOrder> inputOrders = inputOrderService.findByProviderId(id);
        return ResponseEntity.ok().body(inputOrders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(
            @PathVariable Long id
    ) {
        try {
            InputOrder inputOrder = inputOrderService.getInputOrder(id);
            return handleResponse(inputOrder, "Tìm kiếm thành công!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    InputOrderResponse.builder()
                            .message(e.getMessage())
                            .build()
            );
        }

    }


    @DeleteMapping("")
    public ResponseEntity<?> deleteOrder(@RequestBody Map<String, Long[]> request){
        try{
            Long[] ids = request.get("ids");
            inputOrderService.deleteInputOrder(ids);
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
