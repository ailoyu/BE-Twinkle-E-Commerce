package com.twinkle.shopapp.services;

import com.twinkle.shopapp.dtos.ProviderDTO;
import com.twinkle.shopapp.models.Provider;
import com.twinkle.shopapp.responses.ProviderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface IProviderService {

    Provider createProvider(ProviderDTO providerDTO);
    Provider getProviderById(long id);
    List<Provider> getAllProviders();
    Provider updateProvider(long providerId, ProviderDTO providerDTO);
    void deleteProvider(Long[] ids);

    Page<Provider> getProvidersByFilter(String keyword, String address, PageRequest pageRequest);
}
