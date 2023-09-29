package com.twinkle.shopapp.responses;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.twinkle.shopapp.models.Category;
import com.twinkle.shopapp.models.Product;
import com.twinkle.shopapp.models.ProductImage;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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

    @JsonProperty("category_id") // tên trong DB
    private Long categoryId;

    @JsonProperty("category_name") // tên trong DB
    private String categoryName;

    @JsonProperty("product_images")
    private List<ProductImage> productImages = new ArrayList<>();

    // Chuyển từ product -> ProductResponse
    public static ProductResponse fromProduct(Product product){
        ProductResponse productResponse = ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .thumbnail(product.getThumbnail())
                .description(product.getDescription())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .productImages(product.getProductImages())
                .build();
        productResponse.setCreatedAt(product.getCreatedAt());
        productResponse.setUpdatedAt(product.getUpdatedAt());
        return productResponse;
    }

}
