package com.twinkle.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.twinkle.shopapp.models.InputOrder;
import com.twinkle.shopapp.models.Product;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DetailInputOrderDTO {

    @JsonProperty("product_id")
    @Min(value = 1, message = "provider's id phải lớn hơn or bằng 1")
    private Long productId;

    @JsonProperty("input_order_id")
    @Min(value = 1, message = "provider's id phải lớn hơn or bằng 1")
    private Long inputOrderId;

    private Float price; // giá trên 1 sp

    @JsonProperty("quantity")
    private int quantity;
}
