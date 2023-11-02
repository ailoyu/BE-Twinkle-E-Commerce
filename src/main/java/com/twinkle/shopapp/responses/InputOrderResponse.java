package com.twinkle.shopapp.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.twinkle.shopapp.models.InputOrder;
import com.twinkle.shopapp.models.Provider;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InputOrderResponse {
    @JsonProperty("message")
    private String message;

    @JsonProperty("errors")
    private List<String> errors;

    @JsonProperty("input_orders")
    private InputOrder inputOrder;
}
