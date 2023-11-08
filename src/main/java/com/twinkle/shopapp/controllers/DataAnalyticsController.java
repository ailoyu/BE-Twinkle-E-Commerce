package com.twinkle.shopapp.controllers;

import com.twinkle.shopapp.dtos.RevenueStatisticsDTO;
import com.twinkle.shopapp.repositories.CategoryRepository;
import com.twinkle.shopapp.repositories.DetailInputOrderRepository;
import com.twinkle.shopapp.repositories.OrderRepository;
import com.twinkle.shopapp.repositories.ProviderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("${api.prefix}/data-analytics")
@RequiredArgsConstructor
public class DataAnalyticsController {

    private final CategoryRepository categoryRepository;

    private final ProviderRepository providerRepository;

    private final OrderRepository orderRepository;

    private final DetailInputOrderRepository detailInputOrderRepository;

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

    @GetMapping("/size-statistics")
    public ResponseEntity<List<Object[]>> getSizesStatistic() {
        List<Object[]> statistics = detailInputOrderRepository.getSizesStatistic();
        return new ResponseEntity<>(statistics, HttpStatus.OK);
    }

    @GetMapping("/order-status-statistics")
    public ResponseEntity<List<Object[]>> getOrderStatusStatistics() {
        List<Object[]> statistics = orderRepository.getOrderStatusStatistics();
        return new ResponseEntity<>(statistics, HttpStatus.OK);
    }

    @GetMapping("/price-statistics")
    public ResponseEntity<List<Object[]>> getPriceStatistics() {
        List<Object[]> statistics = detailInputOrderRepository.getPriceStatistics();
        return new ResponseEntity<>(statistics, HttpStatus.OK);
    }

    @GetMapping("/revenue-statistics")
    public ResponseEntity<List<RevenueStatisticsDTO>> getRevenue() {
        List<Object[]> statistics = orderRepository.getRevenue();
        List<RevenueStatisticsDTO> formattedStatistics = new ArrayList<>();

        for (Object[] stat : statistics) {
            Date timestamp = new Date(((Date) stat[0]).getTime());
            RevenueStatisticsDTO formattedStat = new RevenueStatisticsDTO();
            formattedStat.setDate(timestamp);
            formattedStat.setValue1(((Number) stat[1]).doubleValue());
            formattedStat.setValue2(((Number) stat[2]).doubleValue());
            formattedStat.setValue3(((Number) stat[3]).doubleValue());
            formattedStat.setValue4(((Number) stat[4]).intValue());
            formattedStatistics.add(formattedStat);
        }

        return new ResponseEntity<>(formattedStatistics, HttpStatus.OK);
    }


}
