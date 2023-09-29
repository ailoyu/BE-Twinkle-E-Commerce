package com.twinkle.shopapp.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.twinkle.shopapp.models.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class RegisterResponse {


    @JsonProperty("message")
    private String message;

    @JsonProperty("user")
    private User user;
}
