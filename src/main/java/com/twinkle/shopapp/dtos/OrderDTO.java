package com.twinkle.shopapp.dtos;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {

    @JsonProperty("user_id")
    @Min(value = 1, message = "User id phải lớn hơn hoặc bằng 1")
    private Long userId;

    @JsonProperty("fullname")
    @NotBlank(message = "Vui lòng nhập họ và tên!")
    private String fullName;

    @NotBlank(message = "Vui lòng nhập email!")
    private String email;

    @JsonProperty("phone_number")
    @NotBlank(message = "Vui lòng nhập số điện thoại!")
//    @Size(min = 5, message = "Số điện thoại phải trên 5 ký tự!")
    private String phoneNumber;

    @NotBlank(message = "Vui lòng nhập địa chỉ!")
    private String address;

    private String status;

    private String note;

    @JsonProperty("total_money")
    @Min(value = 0, message = "Tổng tiền phải lớn hơn 0")
    private Float totalMoney;

    @JsonProperty("shipping_method")
    private String shippingMethod;

    @JsonProperty("shipping_address")
    private String shippingAddress;

    @JsonProperty("shipping_date")
    private LocalDate shippingDate;

    @JsonProperty("payment_method")
    private String paymentMethod;

    @JsonProperty("cart_items")
    @NotEmpty(message = "Vui lòng không để giỏ hàng trống!")
    private List<CartItemDTO> cartItems;


}
