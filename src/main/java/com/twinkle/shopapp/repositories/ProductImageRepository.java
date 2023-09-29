package com.twinkle.shopapp.repositories;

import com.twinkle.shopapp.models.Product;
import com.twinkle.shopapp.models.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    List<ProductImage> findByProductId(Long productId);
    void deleteAllByProduct(Product product);

    void deleteByProductId(Long productId);
}
