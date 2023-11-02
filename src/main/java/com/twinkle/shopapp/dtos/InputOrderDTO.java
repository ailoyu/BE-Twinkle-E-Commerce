package com.twinkle.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.twinkle.shopapp.models.ProductImage;
import com.twinkle.shopapp.models.Provider;
import com.twinkle.shopapp.models.User;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InputOrderDTO {

    @JsonProperty("provider_id")
    @Min(value = 1, message = "provider's id phải lớn hơn or bằng 1")
    private Long providerId;

    @JsonProperty("employee_id")
    @Min(value = 1, message = "employee's id phải lớn hơn or bằng 1")
    private Long employeeId;

//    @JsonProperty("products")
//    @NotEmpty(message = "Vui lòng không để giỏ hàng trống!")
//    private List<ProductDTO> productDTOS;

    private long id; // productId

    @NotBlank(message = "Bạn phải nhập tên sản phẩm!")
    @Size(min = 3, max = 200, message = "Tên sản phẩm phải từ 3 - 200 ký tự!")
    private String name;

    @Min(value = 0, message = "Giá phải lớn hơn hoặc bằng 0")
    @Max(value = 10000000, message = "Giá phải thấp hơn 10,000,000")
    private Float price;

    private String[] images;

    private String description;

    @JsonProperty("category_id") // tên trong DB
    private Long categoryId;

    private List<Integer> quantity;

    private List<Float> sizes;

}
