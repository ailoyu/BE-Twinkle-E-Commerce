package com.twinkle.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginDTO {

    @JsonProperty("phone_number")
    @NotBlank(message = "Vui lòng nhập số điện thoại")
    private String phoneNumber;

    @NotBlank(message = "Vui lòng nhập password")
    private String password;

    @Min(value = 1, message = "You must enter role's Id")
    @JsonProperty("role_id")
    private Long roleId;

    @JsonProperty("new_password")
    private String newPassword;

    @JsonProperty("confirm_new_password")
    private String confirmNewPassword;

}
