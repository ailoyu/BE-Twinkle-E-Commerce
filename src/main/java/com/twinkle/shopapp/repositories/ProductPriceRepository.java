package com.twinkle.shopapp.repositories;

import com.twinkle.shopapp.models.ProductPrice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductPriceRepository extends JpaRepository<ProductPrice, Long> {
    List<ProductPrice> findByProductId(long id);
}
