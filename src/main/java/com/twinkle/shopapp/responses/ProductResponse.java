package com.twinkle.shopapp.responses;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.twinkle.shopapp.models.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductResponse extends BaseResponse{

    private Long id;

    private String name;

    private Float price;

    private String thumbnail;

    private String description;

    private List<Float> sizes;

    private List<Integer> quantity;

    @JsonProperty("category_id") // tên trong DB
    private Long categoryId;

    @JsonProperty("category_name") // tên trong DB
    private String categoryName;

    @JsonProperty("product_images")
    private List<ProductImage> productImages = new ArrayList<>();

    private Provider provider;

    @JsonProperty("input_order_id")
    private long inputOrderId;

    // Chuyển từ product -> ProductResponse
    public static ProductResponse fromProduct(Product product){
        // Map Size và Quantity
        Provider provider = new Provider();
        Map<Float, Integer> sizeToQuantityMap = new HashMap<>();
        for(DetailInputOrder detailInputOrder : product.getDetailInputOrders()){
            Float size = detailInputOrder.getSize();
            int quantity = detailInputOrder.getQuantity();
            provider = detailInputOrder.getInputOrder().getProvider();

            if (sizeToQuantityMap.containsKey(size)) {
                // Nếu kích thước đã tồn tại trong Map, thì cộng thêm quantity vào tổng quantity tương ứng.
                int existingQuantity = sizeToQuantityMap.get(size);
                sizeToQuantityMap.put(size, existingQuantity + quantity);
            } else {
                // Nếu kích thước chưa tồn tại trong Map, thì thêm kích thước và quantity vào Map.
                sizeToQuantityMap.put(size, quantity);
            }
        }

        List<Float> sizes = new ArrayList<>();
        List<Integer> quantity = new ArrayList<>();

        for(Map.Entry<Float, Integer> entry : sizeToQuantityMap.entrySet()){
            Float size = entry.getKey();
            Integer totalQuantity = entry.getValue();
            sizes.add(size);
            quantity.add(totalQuantity);
        }

        // Map Product
        ProductResponse productResponse = ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product
                        .getProductPrices()
                        .get(product.getProductPrices().size() - 1)
                        .getPrice())
                .thumbnail(product.getThumbnail())
                .quantity(quantity)
                .sizes(sizes)
                .description(product.getDescription())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .productImages(product.getProductImages())
                .provider(provider)
//                .inputOrderId(product
//                        .getDetailInputOrders()
//                        .get(product.getDetailInputOrders().size() - 1)
//                        .getInputOrder().getId())
                .build();
        productResponse.setCreatedAt(product.getCreatedAt());
        productResponse.setUpdatedAt(product.getUpdatedAt());
        return productResponse;
    }

}
