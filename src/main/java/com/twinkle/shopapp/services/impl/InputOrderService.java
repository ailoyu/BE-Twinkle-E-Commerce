package com.twinkle.shopapp.services.impl;

import com.twinkle.shopapp.dtos.DetailInputOrderDTO;
import com.twinkle.shopapp.dtos.InputOrderDTO;
import com.twinkle.shopapp.dtos.ProductDTO;
import com.twinkle.shopapp.exceptions.DataNotFoundException;
import com.twinkle.shopapp.models.*;
import com.twinkle.shopapp.repositories.*;
import com.twinkle.shopapp.services.IInputOrderService;
import com.twinkle.shopapp.utils.ImageUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InputOrderService implements IInputOrderService {

    @Autowired
    private ImageUtils imageUtils;

    private final InputOrderRepository inputOrderRepository;

    private final DetailInputOrderRepository detailInputOrderRepository;

    private final ProviderRepository providerRepository;

    private final EmployeeRepository employeeRepository;

    private final ProductRepository productRepository;

    private final CategoryRepository categoryRepository;

    private final ProductImageRepository productImageRepository;

    private final ProductPriceRepository productPriceRepository;

    @Override
    @Transactional // Rollback data in case of an error
    public InputOrder createInputOrder(InputOrderDTO inputOrderDTO) throws DataNotFoundException {
        // Retrieve the provider
        Provider provider = providerRepository.findById(inputOrderDTO.getProviderId())
                .orElseThrow(() -> new DataNotFoundException("Provider not found"));

        // Retrieve the employee
        Employee employee = employeeRepository.findById(inputOrderDTO.getEmployeeId())
                .orElseThrow(() -> new DataNotFoundException("Employee not found"));

        // Create the InputOrder
        InputOrder inputOrder = InputOrder.builder()
                .provider(provider)
                .employee(employee)
                .build();

        // Save the InputOrder
        InputOrder savedInputOrder = inputOrderRepository.save(inputOrder);

        Product newProduct = Product.builder()
                .description(inputOrderDTO.getDescription())
                .name(inputOrderDTO.getName())
                .category(categoryRepository.findById(inputOrderDTO.getCategoryId())
                        .orElseThrow(() -> new DataNotFoundException("Category not found")))
                .active(true)
                .build();
        // Save the Product
        Product savedProduct = productRepository.save(newProduct);

        // Set the product price
//        Float taxFee = 0.1f * inputOrderDTO.getPrice();
//        Float deliveryShipFee = 5f;
        Float profitFee = 0.2f * inputOrderDTO.getPrice();
        Float totalFee = inputOrderDTO.getPrice() + profitFee;
        ProductPrice productPrice = ProductPrice.builder()
                .product(savedProduct)
                .price(totalFee)
                .appliedDate(new Date())
                .build();

        // Set product price to return value
        productPriceRepository.save(productPrice);


        // Xử lý detailProduct
        List<DetailInputOrder> detailInputOrders = new ArrayList<>();
        int i = 0;
        for(float size : inputOrderDTO.getSizes()){

            DetailInputOrder detailInputOrder = DetailInputOrder.builder()
                    .inputOrder(savedInputOrder)
                    .price(inputOrderDTO.getPrice())
                    .size(size)
                    .quantity(inputOrderDTO.getQuantity().get(i))
                    .product(savedProduct)
                    .build();
            ++i;
            // Save the DetailInputOrder
            DetailInputOrder savedDetailInputOrder = detailInputOrderRepository.save(detailInputOrder);
            detailInputOrders.add(savedDetailInputOrder);
        }

        // Xử lý productImage
        for (String imageURL : inputOrderDTO.getImages()) {
            ProductImage productImage = ProductImage.builder()
                        .product(savedProduct)
                        .imageUrl(imageUtils.storeFileWithBase64(imageURL))
                        .build();

                // Set the first image as the thumbnail
                if (imageURL.equals(inputOrderDTO.getImages()[0])) {
                    newProduct.setThumbnail(imageUtils.storeFileWithBase64(imageURL));
                }
                productImageRepository.save(productImage);
        }
        savedInputOrder.setDetailInputOrders(detailInputOrders);
        return savedInputOrder;
    }

    @Override
    @Transactional // rollback dữ liệu khi bị sai gì đó
    public InputOrder updateInputOrder(Long id, InputOrderDTO inputOrderDTO) throws DataNotFoundException {
        InputOrder existingInputOrder = getInputOrder(id);
        List<DetailInputOrder> detailInputOrderList = detailInputOrderRepository.findByProductId(existingInputOrder.getId());
        detailInputOrderRepository.deleteAll(detailInputOrderList);


        Provider provider = providerRepository.findById(inputOrderDTO.getProviderId())
                .orElseThrow(() -> new DataNotFoundException("Ko tìm thấy provider này"));

        Employee employee = employeeRepository.findById(inputOrderDTO.getEmployeeId())
                .orElseThrow(() -> new DataNotFoundException("Ko tìm thấy employee này"));


        existingInputOrder.setEmployee(employee);
        existingInputOrder.setProvider(provider);

        // tìm product -> cập nhật lại product
        Product product = productRepository.findById(inputOrderDTO.getId())
                .orElseThrow(() -> new DataNotFoundException("Ko tìm thấy product_id"));

        product.setDescription(inputOrderDTO.getDescription());
        product.setName(inputOrderDTO.getName());
        product.setActive(true);
        product.setCategory(categoryRepository.findById(inputOrderDTO.getCategoryId())
                .orElseThrow(() -> new DataNotFoundException("Ko tìm thấy category")));

        Product savedProduct = productRepository.save(product);

        // save new productPrice
//        Float taxFee = 0.1f * inputOrderDTO.getPrice();
//        Float deliveryShipFee = 5f;
        Float profitFee = 0.2f * inputOrderDTO.getPrice();
        Float totalFee = inputOrderDTO.getPrice() + profitFee;
        ProductPrice newProductPrice = ProductPrice.builder()
                .product(savedProduct)
                .price(totalFee)
                .appliedDate(new Date())
                .build();

        productPriceRepository.save(newProductPrice);

        // Xóa product_image -> tạo lại product_image
        productImageRepository.deleteAllByProduct(product);


        // Xử lý detailProduct
        List<DetailInputOrder> detailInputOrders = new ArrayList<>();
        int i = 0;
        for(float size : inputOrderDTO.getSizes()){

            DetailInputOrder detailInputOrder = DetailInputOrder.builder()
                    .inputOrder(existingInputOrder)
                    .price(inputOrderDTO.getPrice())
                    .size(size)
                    .quantity(inputOrderDTO.getQuantity().get(i))
                    .product(savedProduct)
                    .build();
            ++i;
            // Save the DetailInputOrder
            DetailInputOrder savedDetailInputOrder = detailInputOrderRepository.save(detailInputOrder);
            detailInputOrders.add(savedDetailInputOrder);
        }

        // Xử lý productImage

        if(inputOrderDTO.getImages().length > 0){
            List<String> newImages = Arrays.stream(inputOrderDTO.getImages())
                    .map(image -> {
                        return image.startsWith("data") ?
                                imageUtils.storeFileWithBase64(image)
                                : image;
                    })
                    .collect(Collectors.toList());

            productImageRepository.deleteAllByProduct(savedProduct);

            for(String image : newImages){
                ProductImage productImage = new ProductImage();
                productImage.setProduct(savedProduct);
                productImage.setImageUrl(image);

                // Set ảnh đầu tiên làm thumbnail
                if(image.equals(newImages.get(0))){
                    savedProduct.setThumbnail(image);
                    productRepository.save(savedProduct);
                }
                productImageRepository.save(productImage);
            }
        }

        existingInputOrder.setDetailInputOrders(detailInputOrders);

        return inputOrderRepository.save(existingInputOrder);
    }

    @Override
    public InputOrder getInputOrder(Long id) throws DataNotFoundException {
        InputOrder inputOrder = inputOrderRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Ko tìm thấy employee này"));
        return inputOrder;
    }



    @Override
    public void deleteInputOrder(Long[] ids) throws DataNotFoundException {
        for(long id : ids){
            Optional<InputOrder> optionalInputOrder = inputOrderRepository.findById(id);
            if(optionalInputOrder.isPresent()){
                inputOrderRepository.delete(optionalInputOrder.get());
            }
        }
    }

    @Override
    public List<InputOrder> findByProviderId(Long providerId) {
        return inputOrderRepository.findByProviderId(providerId);
    }


}
