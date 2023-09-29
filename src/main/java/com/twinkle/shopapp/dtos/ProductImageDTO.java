package com.twinkle.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.twinkle.shopapp.models.Product;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductImageDTO {

    @JsonProperty("product_id")
    @Min(value = 1, message = "product's id phải lớn hơn or bằng 1")
    private Long productId;

    @Size(min = 5, max = 200, message = "Tên phải từ 5 tới 200 ký tự")
    @JsonProperty("image_url")
    private String imageUrl;
}
