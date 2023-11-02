package com.twinkle.shopapp.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.twinkle.shopapp.models.DetailInputOrder;
import com.twinkle.shopapp.models.InputOrder;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DetailInputOrderResponse {
    @JsonProperty("message")
    private String message;

    @JsonProperty("errors")
    private List<String> errors;

    @JsonProperty("detail_input_orders")
    private DetailInputOrder detailInputOrder;
}
