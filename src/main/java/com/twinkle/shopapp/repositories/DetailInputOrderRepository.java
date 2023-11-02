package com.twinkle.shopapp.repositories;

import com.twinkle.shopapp.models.DetailInputOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DetailInputOrderRepository extends JpaRepository<DetailInputOrder, Long> {
    List<DetailInputOrder> findByInputOrderId(Long id);


}
