package com.twinkle.shopapp.services.impl;

import com.twinkle.shopapp.dtos.ProviderDTO;
import com.twinkle.shopapp.models.Product;
import com.twinkle.shopapp.models.Provider;
import com.twinkle.shopapp.repositories.ProviderRepository;
import com.twinkle.shopapp.responses.ProductResponse;
import com.twinkle.shopapp.responses.ProviderResponse;
import com.twinkle.shopapp.services.IProviderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProviderService implements IProviderService {

    private final ProviderRepository providerRepository;

    @Override
    public Provider createProvider(ProviderDTO providerDTO) {
        // Parse từ DTO sang model
        Provider provider = Provider.builder()
                .name(providerDTO.getName())
                .address(providerDTO.getAddress())
                .email(providerDTO.getEmail())
                .description(providerDTO.getDescription())
                .website(providerDTO.getWebsite())
                .phoneNumber(providerDTO.getPhoneNumber())
                .build();
        return providerRepository.save(provider);
    }

    @Override
    public Provider getProviderById(long id) {
        return providerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ko tìm thấy nhà cung cấp"));
    }

    @Override
    public List<Provider> getAllProviders() {
        return providerRepository.findAll();
    }

    @Override
    public Provider updateProvider(long providerId, ProviderDTO providerDTO) {
//        Category existingCategory = getCategoryById(categoryId);
//        existingCategory.setName(categoryDTO.getName());
//        return categoryRepository.save(existingCategory);

        Provider existingProvider = getProviderById(providerId);
        existingProvider.setName(providerDTO.getName());
        existingProvider.setAddress(providerDTO.getAddress());
        existingProvider.setEmail(providerDTO.getEmail());
        existingProvider.setDescription(providerDTO.getDescription());
        existingProvider.setWebsite(providerDTO.getWebsite());
        existingProvider.setPhoneNumber(providerDTO.getPhoneNumber());

        return providerRepository.save(existingProvider);
    }

    @Override
    public void deleteProvider(Long[] ids) {
        for(long id : ids){
            Optional<Provider> optionalProvider = providerRepository.findById(id);
            if(optionalProvider.isPresent()){
                providerRepository.delete(optionalProvider.get()); // nếu có product trong DB ms xóa

            }
        }
    }

    @Override
    public Page<Provider> getProvidersByFilter(String keyword, String address, PageRequest pageRequest) {
        // Lấy danh sách sản phẩm theo page hiện tại và limit
        return providerRepository.searchProviders(keyword, address, pageRequest);
    }
}
