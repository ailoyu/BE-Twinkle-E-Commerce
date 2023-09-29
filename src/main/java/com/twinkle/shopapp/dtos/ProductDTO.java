package com.twinkle.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDTO {

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


}
