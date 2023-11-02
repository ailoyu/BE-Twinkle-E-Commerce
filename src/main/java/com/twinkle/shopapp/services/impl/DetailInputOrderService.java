package com.twinkle.shopapp.services.impl;

import com.twinkle.shopapp.dtos.DetailInputOrderDTO;
import com.twinkle.shopapp.exceptions.DataNotFoundException;
import com.twinkle.shopapp.models.DetailInputOrder;
import com.twinkle.shopapp.models.InputOrder;
import com.twinkle.shopapp.models.Product;
import com.twinkle.shopapp.repositories.DetailInputOrderRepository;
import com.twinkle.shopapp.repositories.InputOrderRepository;
import com.twinkle.shopapp.repositories.ProductRepository;
import com.twinkle.shopapp.services.IDetailInputOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DetailInputOrderService implements IDetailInputOrderService {

    private final InputOrderRepository inputOrderRepository;

    private final ProductRepository productRepository;

    private final DetailInputOrderRepository detailInputOrderRepository;

    @Override
    public DetailInputOrder createDetailInputOrder(DetailInputOrderDTO detailInputOrderDTO) throws DataNotFoundException {

        Product product = productRepository.findById(detailInputOrderDTO.getProductId())
                .orElseThrow(() -> new DataNotFoundException("Ko tìm thấy product id này!"));

        InputOrder inputOrder = inputOrderRepository.findById(detailInputOrderDTO.getInputOrderId())
                .orElseThrow(() -> new DataNotFoundException("Ko tìm thấy input order này"));

        DetailInputOrder detailInputOrder = DetailInputOrder.builder()
                .inputOrder(inputOrder)
                .product(product)
                .price(detailInputOrderDTO.getPrice())
                .quantity(detailInputOrderDTO.getQuantity())
                .build();

        return detailInputOrderRepository.save(detailInputOrder);
    }

    @Override
    public DetailInputOrder getDetailInputOrder(Long id) throws DataNotFoundException {
        DetailInputOrder detailInputOrder = detailInputOrderRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Ko tìm detail input order này!"));
        return detailInputOrder;
    }

    @Override
    public DetailInputOrder updateDetailInputOrder(Long id, DetailInputOrderDTO detailInputOrderDTO) throws DataNotFoundException {
        DetailInputOrder existingDetailInputOrder = getDetailInputOrder(id);
        Product product = productRepository.findById(detailInputOrderDTO.getProductId())
                .orElseThrow(() -> new DataNotFoundException("Ko tìm thấy product id này!"));

        InputOrder inputOrder = inputOrderRepository.findById(detailInputOrderDTO.getInputOrderId())
                .orElseThrow(() -> new DataNotFoundException("Ko tìm thấy input order này"));

        existingDetailInputOrder.setInputOrder(inputOrder);
        existingDetailInputOrder.setProduct(product);
        existingDetailInputOrder.setPrice(detailInputOrderDTO.getPrice());
        existingDetailInputOrder.setQuantity(detailInputOrderDTO.getQuantity());

        return detailInputOrderRepository.save(existingDetailInputOrder);
    }

    @Override
    public void deleteDetailInputOrder(Long[] ids) throws DataNotFoundException {
        for(long id : ids){
            Optional<DetailInputOrder> optionalDetailInputOrder = detailInputOrderRepository.findById(id);
            if(optionalDetailInputOrder.isPresent()){
                detailInputOrderRepository.delete(optionalDetailInputOrder.get());
            }
        }
    }

    @Override
    public List<DetailInputOrder> findByInputOrder(Long inputOrderId) {
        return detailInputOrderRepository.findByInputOrderId(inputOrderId);
    }


}
