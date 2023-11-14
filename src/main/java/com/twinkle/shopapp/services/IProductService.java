package com.twinkle.shopapp.services;

import com.twinkle.shopapp.dtos.ProductDTO;
import com.twinkle.shopapp.dtos.ProductImageDTO;
import com.twinkle.shopapp.exceptions.DataNotFoundException;
import com.twinkle.shopapp.exceptions.InvalidParamException;
import com.twinkle.shopapp.models.Product;
import com.twinkle.shopapp.models.ProductImage;
import com.twinkle.shopapp.responses.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IProductService {
    Product createProduct(ProductDTO productDTO) throws Exception;
    Product getProductById(long id) throws Exception;
    Page<ProductResponse> getAllProducts(String keyword, Long categoryId, Float size, String selectedPriceRate, String selectedProvider, PageRequest pageRequest);
    Product updateProduct(long id, ProductDTO productDTO) throws Exception;
    void deleteProduct(Long[] id);
    boolean existsByName(String name);

    List<Float>getAllAvailableSizes();

    public List<Product> findProductByIds(List<Long> productIds);
    ProductImage createProductImage(ProductImageDTO productImageDTO) throws Exception;

    List<ProductResponse> getAllBestSellers();

    List<ProductResponse> getProductsByCategory(Long categoryId);

    List<ProductResponse> getProductsByBrands(String brands);

    List<ProductResponse> getNewProducts();

}
