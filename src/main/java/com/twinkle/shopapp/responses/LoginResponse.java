package com.twinkle.shopapp.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class LoginResponse {

    @JsonProperty("id")
    private long id;

    @JsonProperty("message")
    private String message;

    @JsonProperty("fullname")
    private String fullName;

    @JsonProperty("phone_number")
    private String phoneNumber;

    @JsonProperty("address")
    private String address;

    @JsonProperty("token")
    private String token;

    @JsonProperty("role_id")
    private long roleId;

    @JsonProperty("avatar")
    private String avatar;

    @JsonProperty("date_of_birth")
    private String dateOfBirth;

    @JsonProperty("employee_id")
    private long employeeId;



}
