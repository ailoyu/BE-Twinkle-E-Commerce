package com.twinkle.shopapp.controllers;

import com.twinkle.shopapp.repositories.CategoryRepository;
import com.twinkle.shopapp.repositories.ProviderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/data-analytics")
@RequiredArgsConstructor
public class DataAnalyticsController {

    private final CategoryRepository categoryRepository;

    private final ProviderRepository providerRepository;

    @GetMapping("/category-statistics")
    public ResponseEntity<List<Object[]>> getCategoryStatistics() {
        List<Object[]> statistics = categoryRepository.getCategoryStatistics();
        return new ResponseEntity<>(statistics, HttpStatus.OK);
    }

    @GetMapping("/provider-statistics")
    public ResponseEntity<List<Object[]>> getProviderStatistics() {
        List<Object[]> statistics = providerRepository.getProviderStatistics();
        return new ResponseEntity<>(statistics, HttpStatus.OK);
    }

    @GetMapping("/product-statistics")
    public ResponseEntity<List<Object[]>> getQuantityOfEachProductStatistics() {
        List<Object[]> statistics = providerRepository.getQuantityOfEachProductStatistics();
        return new ResponseEntity<>(statistics, HttpStatus.OK);
    }




}
