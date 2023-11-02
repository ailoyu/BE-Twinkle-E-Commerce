package com.twinkle.shopapp.responses;

import com.twinkle.shopapp.models.Provider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class ProviderListResponse {

    private List<Provider> providers;

    private int totalPage;
}
