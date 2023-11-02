package com.twinkle.shopapp.services;

import com.twinkle.shopapp.dtos.DetailInputOrderDTO;
import com.twinkle.shopapp.dtos.InputOrderDTO;
import com.twinkle.shopapp.exceptions.DataNotFoundException;
import com.twinkle.shopapp.models.DetailInputOrder;
import com.twinkle.shopapp.models.InputOrder;

import java.util.List;

public interface IDetailInputOrderService {

    DetailInputOrder createDetailInputOrder(DetailInputOrderDTO detailInputOrderDTO) throws DataNotFoundException;
    DetailInputOrder getDetailInputOrder(Long id) throws DataNotFoundException;
    DetailInputOrder updateDetailInputOrder(Long id, DetailInputOrderDTO detailInputOrderDTO) throws DataNotFoundException;
    void deleteDetailInputOrder(Long[] ids) throws DataNotFoundException;
    List<DetailInputOrder> findByInputOrder(Long inputOrderId);
}
