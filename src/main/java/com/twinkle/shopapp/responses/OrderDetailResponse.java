package com.twinkle.shopapp.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.twinkle.shopapp.models.Order;
import com.twinkle.shopapp.models.OrderDetail;
import com.twinkle.shopapp.models.Product;
import jakarta.validation.constraints.Min;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDetailResponse {

    private Long id;

    @JsonProperty("order_id")
    private Long orderId;

    @JsonProperty("product_id")
    private Long productId;

    private Float price;

    @JsonProperty("number_of_products")
    private int numberOfProducts;

    private String color;

    // Chuyển từ product -> ProductResponse
    public static OrderDetailResponse fromOrderDetail(OrderDetail orderDetail) {
        return OrderDetailResponse
                .builder()
                .id(orderDetail.getId())
                .orderId(orderDetail.getOrder().getId())
                .productId(orderDetail.getProduct().getId())
                .price(orderDetail
                        .getProduct()
                        .getProductPrices()
                        .get(orderDetail.getProduct().getProductPrices().size() - 1)
                        .getPrice())
                .numberOfProducts(orderDetail.getNumberOfProducts())
                .build();
    }

}
