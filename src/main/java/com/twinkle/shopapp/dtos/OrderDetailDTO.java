package com.twinkle.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailDTO {

    @JsonProperty("order_id")
    @Min(value = 1, message = "orderId phải lớn hoặc bằng 1")
    private Long orderId;

    @JsonProperty("product_id")
    @Min(value = 1, message = "productId phải lớn hoặc bằng 1")
    private Long productId;

    @Min(value = 0, message = "orderId phải lớn hoặc bằng 0")
    private Float price;

    @JsonProperty("number_of_products")
    @Min(value = 1, message = "số lượng sản phẩm phải lớn hoặc bằng 1")
    private int numberOfProducts;

    @JsonProperty("total_money")
    @Min(value = 0, message = "số tiền phải lớn hoặc bằng 0 (có thể khuyến mãi 100%)")
    private Float totalMoney;


}
