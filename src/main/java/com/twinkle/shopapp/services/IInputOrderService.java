package com.twinkle.shopapp.services;

import com.twinkle.shopapp.dtos.InputOrderDTO;
import com.twinkle.shopapp.dtos.OrderDTO;
import com.twinkle.shopapp.exceptions.DataNotFoundException;
import com.twinkle.shopapp.models.InputOrder;
import com.twinkle.shopapp.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IInputOrderService {
    InputOrder createInputOrder(InputOrderDTO inputOrderDTO) throws DataNotFoundException;
    InputOrder getInputOrder(Long id) throws DataNotFoundException;
    InputOrder updateInputOrder(Long id, InputOrderDTO inputOrderDTO) throws DataNotFoundException;
    void deleteInputOrder(Long[] ids) throws DataNotFoundException;
    List<InputOrder> findByProviderId(Long providerId);
}
