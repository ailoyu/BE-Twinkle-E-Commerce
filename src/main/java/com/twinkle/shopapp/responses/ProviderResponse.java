package com.twinkle.shopapp.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.twinkle.shopapp.models.Category;
import com.twinkle.shopapp.models.Provider;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProviderResponse {
    @JsonProperty("message")
    private String message;

    @JsonProperty("errors")
    private List<String> errors;

    @JsonProperty("provider")
    private Provider provider;
}
