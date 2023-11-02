package com.twinkle.shopapp.repositories;

import com.twinkle.shopapp.models.InputOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InputOrderRepository extends JpaRepository<InputOrder, Long> {

    List<InputOrder> findByProviderId(Long providerId);

}
