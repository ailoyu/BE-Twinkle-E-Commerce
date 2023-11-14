package com.twinkle.shopapp.services.impl;

import com.twinkle.shopapp.dtos.ProductDTO;
import com.twinkle.shopapp.dtos.ProductImageDTO;
import com.twinkle.shopapp.exceptions.DataNotFoundException;
import com.twinkle.shopapp.exceptions.InvalidParamException;
import com.twinkle.shopapp.models.Category;
import com.twinkle.shopapp.models.Product;
import com.twinkle.shopapp.models.ProductImage;
import com.twinkle.shopapp.models.ProductPrice;
import com.twinkle.shopapp.repositories.CategoryRepository;
import com.twinkle.shopapp.repositories.ProductImageRepository;
import com.twinkle.shopapp.repositories.ProductPriceRepository;
import com.twinkle.shopapp.repositories.ProductRepository;
import com.twinkle.shopapp.responses.ProductResponse;
import com.twinkle.shopapp.services.IProductService;
import com.twinkle.shopapp.utils.ImageUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {

    private final ProductRepository productRepository;

    private final CategoryRepository categoryRepository;

    private final ProductImageRepository productImageRepository;

    private final ProductPriceRepository productPriceRepository;

    @Autowired
    private ImageUtils imageUtils;

    @Override
    @Transactional
    public Product createProduct(ProductDTO productDTO) throws DataNotFoundException, IOException {
        Category existingCategory = categoryRepository
                .findById(productDTO.getCategoryId())
                .orElseThrow(
                () -> new DataNotFoundException("Ko tìm thấy thể loại vs id = " + productDTO.getCategoryId()));

        Product newProduct = Product
                .builder()
                .name(productDTO.getName())
                .category(existingCategory)
                .description(productDTO.getDescription())
                .build();

        Product product = productRepository.save(newProduct);

        if(productDTO.getImages().length > 0){
            for(String imageURL : productDTO.getImages()){
                ProductImage productImage = new ProductImage();
                productImage.setProduct(product);
                productImage.setImageUrl(imageUtils.storeFileWithBase64(imageURL));

                // Set ảnh đầu tiên làm thumbnail
                if(imageURL.equals(productDTO.getImages()[0])){
                    newProduct.setThumbnail(imageUtils.storeFileWithBase64(imageURL));
                    productRepository.save(newProduct);
                }

                productImageRepository.save(productImage);
            }
        }
        return product;
    }

    @Override
    public Product getProductById(long id) throws Exception {
        return productRepository.findByIdAndActive(id, true)
                .orElseThrow(() -> new DataNotFoundException("Ko tìm thấy id " + id));
    }

    public List<Product> findProductByIds(List<Long> productIds){
        return productRepository.findActiveProductsByIdIn(productIds);
    }

    @Override
    public Page<ProductResponse> getAllProducts(String keyword, Long categoryId, Float size,
                                                String selectedPriceRate,
                                                String selectedProvider,
                                                PageRequest pageRequest) {

        return productRepository.searchProducts(categoryId, keyword, size, selectedPriceRate, selectedProvider, pageRequest).map(product ->
                // parse từ product -> ProductResponse: kết quả muốn trả ra client
                ProductResponse.fromProduct(product));
     }

    @Override
    @Transactional
    public Product updateProduct(long id, ProductDTO productDTO) throws Exception {
        Product existingProduct = getProductById(id);
        if(existingProduct != null){
            Category existingCategory = categoryRepository
                    .findById(productDTO.getCategoryId())
                    .orElseThrow(
                            () -> new DataNotFoundException("Ko tìm thấy thể loại vs id = " + productDTO.getCategoryId()));

            // Convert từ DTO -> Product (Dùng ModelMapper or Object Mapper)
            existingProduct.setName(productDTO.getName());
            existingProduct.setCategory(existingCategory);
//            existingProduct.setPrice(productDTO.getPrice());
            existingProduct.setDescription(productDTO.getDescription());

            // cập nhật lại product Price
            ProductPrice productPrice = new ProductPrice();
            productPrice.setPrice(productDTO.getPrice());
            productPrice.setAppliedDate(new Date());
            productPrice.setProduct(existingProduct);
            productPriceRepository.save(productPrice);

//            List<String> newImages = new ArrayList<>();
//             if(productDTO.getImages().length > 0){
//                 for(String image : productDTO.getImages()){
//                     String imageUrl;
//                     if(image.startsWith("http")){ // đường dẫn ảnh localhost://
//                         imageUrl = image.substring(image.lastIndexOf("/") + 1);
//                     } else { // đường dẫn ảnh BASE64
//                         imageUrl = ImageUtils.storeFileWithBase64(image);
//                     }
//                     newImages.add(imageUrl);
//                 }
//                 productImageRepository.deleteAllByProduct(existingProduct);
//             }


            if(productDTO.getImages().length > 0){
                List<String> newImages = Arrays.stream(productDTO.getImages())
                        .map(image -> {
                            return image.startsWith("data") ?
                                    imageUtils.storeFileWithBase64(image)
                                    : image;
                        })
                        .collect(Collectors.toList());

                productImageRepository.deleteAllByProduct(existingProduct);

                for(String image : newImages){
                    ProductImage productImage = new ProductImage();
                    productImage.setProduct(existingProduct);
                    productImage.setImageUrl(image);

                    // Set ảnh đầu tiên làm thumbnail
                    if(image.equals(newImages.get(0))){
                        existingProduct.setThumbnail(image);
                        productRepository.save(existingProduct);
                    }
                    productImageRepository.save(productImage);
                }
            }

            return productRepository.save(existingProduct);

        }
        return null;
    }

    @Override
    @Transactional
    public void deleteProduct(Long[] ids) {
        for(long id : ids){
            Optional<Product> optionalProduct = productRepository.findById(id);
            if(optionalProduct.isPresent()){
                optionalProduct.get().setActive(false);
                productRepository.save(optionalProduct.get());
            }
        }
    }

    @Override
    public boolean existsByName(String name) {
        return productRepository.existsByName(name);
    }

    @Override
    public List<Float> getAllAvailableSizes() {
        return productRepository.getAllAvailableSizes();
    }

    @Override
    public ProductImage createProductImage(
            ProductImageDTO productImageDTO) throws Exception {
        Product existingProduct = productRepository
                .findById(productImageDTO.getProductId())
                .orElseThrow(
                        () -> new DataNotFoundException("Ko tìm thấy sản phẩm vs id = " +
                                productImageDTO.getProductId()));

        ProductImage newProductImage = ProductImage
                .builder()
                .product(existingProduct)
                .imageUrl(productImageDTO.getImageUrl())
                .build();

        // Ko cho insert quá 5 ảnh cho 1 sản phẩm
        int size = productImageRepository.findByProductId(productImageDTO.getProductId()).size();
        if(size >= 5){
            throw new InvalidParamException(
                    "Sản phẩm này đã có "+size + " ảnh");
        }
        return productImageRepository.save(newProductImage);

    }

    @Override
    public List<ProductResponse> getAllBestSellers() {

        List<ProductResponse> bestSellers = new ArrayList<>();
        for (Product product : productRepository.getAllBestSellers()) {
            bestSellers.add(ProductResponse.fromProduct(product));
        }
        return bestSellers;
    }

    @Override
    public List<ProductResponse> getProductsByCategory(Long categoryId) {
        List<ProductResponse> productByCategory = new ArrayList<>();
        for (Product product : productRepository.getProductsByCategory(categoryId)) {
            productByCategory.add(ProductResponse.fromProduct(product));
        }
        return productByCategory;
    }

    @Override
    public List<ProductResponse> getProductsByBrands(String brands) {
        List<ProductResponse> productByCategory = new ArrayList<>();
        for (Product product : productRepository.findProductsByBrand(brands)) {
            productByCategory.add(ProductResponse.fromProduct(product));
        }
        return productByCategory;
    }

    @Override
    public List<ProductResponse> getNewProducts() {
        List<ProductResponse> productByCategory = new ArrayList<>();
        for (Product product : productRepository.getNewProducts()) {
            productByCategory.add(ProductResponse.fromProduct(product));
        }
        return productByCategory;
    }

}
