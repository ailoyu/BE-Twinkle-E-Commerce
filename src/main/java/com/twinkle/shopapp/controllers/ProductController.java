package com.twinkle.shopapp.controllers;

import com.github.javafaker.Faker;
import com.twinkle.shopapp.component.LocalizationUtils;
import com.twinkle.shopapp.configuration.VNPayConfig;
import com.twinkle.shopapp.dtos.CategoryDTO;
import com.twinkle.shopapp.dtos.OrderDTO;
import com.twinkle.shopapp.dtos.ProductDTO;
import com.twinkle.shopapp.dtos.ProductImageDTO;
import com.twinkle.shopapp.exceptions.DataNotFoundException;
import com.twinkle.shopapp.models.Order;
import com.twinkle.shopapp.models.Product;
import com.twinkle.shopapp.models.ProductImage;
import com.twinkle.shopapp.repositories.OrderRepository;
import com.twinkle.shopapp.repositories.ProductRepository;
import com.twinkle.shopapp.repositories.UserRepository;
import com.twinkle.shopapp.responses.CategoryResponse;
import com.twinkle.shopapp.responses.ProductListResponse;
import com.twinkle.shopapp.responses.ProductResponse;
import com.twinkle.shopapp.services.IProductService;
import com.twinkle.shopapp.utils.EmailUtils;
import com.twinkle.shopapp.utils.ImageUtils;
import com.twinkle.shopapp.utils.MessageKeys;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${api.prefix}/products")
@RequiredArgsConstructor
public class ProductController {

    private final IProductService productService;

    private final LocalizationUtils localizationUtils;

    private final ModelMapper modelMapper;

    @PostMapping("")
    public ResponseEntity<?> insertProduct(
            @Valid @RequestBody ProductDTO productDTO,
            BindingResult result) {
        try {
            if (result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(fieldError -> fieldError.getDefaultMessage())
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }

            //lưu vào đối tượng product trong DB
            Product newProduct = productService.createProduct(productDTO);
            return ResponseEntity.ok(newProduct);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(
            @PathVariable("id") Long productId,
            @Valid @RequestBody ProductDTO productDTO){
        try{
            Product updatedProduct = productService.updateProduct(productId, productDTO);
            return ResponseEntity.ok().body(updatedProduct);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


//    @PostMapping(value = "uploads/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<?> uploadImages(
//            @PathVariable("id") Long productId,
//            @ModelAttribute List<MultipartFile> files
//    ){
//        try {
//            // Lấy product ra để lưu productImages
//            Product existingProduct = productService.getProductById(productId);
//            List<ProductImage> productImages = new ArrayList<>();
//
//            // Nếu chèn 1 lần quá 5 ảnh thì ko chấp nhận
//            if(files.size() > 6)
//                return ResponseEntity.badRequest().body(localizationUtils
//                        .getLocalizedMessage(MessageKeys.UPLOAD_IMAGES_MAX_5));
//
//            // lưu nhìu ảnh trong product đó
//            files = (files == null) ? new ArrayList<MultipartFile>() : files;
//            for (MultipartFile file : files) {
//                if(file.getSize() == 0) {
//                    continue;
//                }
//                // Kiểm tra kích thước file
//                if (file.getSize() > 10 * 1024 * 1024) { // Kích thước > 10MB
////            throw new ResponseStatusException(
////                    HttpStatus.PAYLOAD_TOO_LARGE, "Kích thước file ảnh quá lớn! Tối đa là 10MB");
//                    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
//                            .body(localizationUtils
//                                    .getLocalizedMessage(MessageKeys.UPLOAD_IMAGES_FILE_LARGE));
//                }
//
//
//                // Kiểm tra phải file ảnh ko?
//                String contentType = file.getContentType();
//                if (contentType == null || !contentType.startsWith("image/")) {
//                    return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
//                            .body(localizationUtils.getLocalizedMessage(MessageKeys.UPLOAD_IMAGES_FILE_MUST_BE_IMAGE));
//                }
//
//                // Lưu hình ảnh xuống file
//                String filename = ImageUtils.storeFile(file);
//
//                //lưu vào bảng product_images
//                ProductImage productImage = productService.createProductImage(
//                        new ProductImageDTO().builder()
//                                .productId(productId)
//                                .imageUrl(filename)
//                                .build());
//
//                productImages.add(productImage);
//            }
//            return ResponseEntity.ok().body(productImages);
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }
//
//    @GetMapping("/images/{imageName}") // Xem lại ảnh sau khi upload
//    public ResponseEntity<?> viewImage(@PathVariable String imageName) {
//        try {
//            java.nio.file.Path imagePath = Paths.get("uploads/"+imageName);
//            UrlResource resource = new UrlResource(imagePath.toUri());
//
//            if (resource.exists()) {
//                return ResponseEntity.ok()
//                        .contentType(MediaType.IMAGE_JPEG)
//                        .body(resource);
//            } else {
//                return ResponseEntity.ok()
//                        .contentType(MediaType.IMAGE_JPEG)
//                        .body(new UrlResource(Paths.get("uploads/notfound.jpg").toUri()));
//            }
//        } catch (Exception e) {
//            return ResponseEntity.notFound().build();
//        }
//    }


    private final ProductRepository productRepository;

    @GetMapping("")
    public ResponseEntity<ProductListResponse> getAllProducts(
            @RequestParam(defaultValue = "") String keyword, // search
            @RequestParam(defaultValue = "0", name = "category_id") Long categoryId, // tìm theo thể loại
            @RequestParam(defaultValue = "0", name = "size") Float size,
            @RequestParam(defaultValue = "", name = "order_by") String orderBy,
            @RequestParam(defaultValue = "", name = "selected_price_rate") String selectedPriceRate,
            @RequestParam(defaultValue = "", name = "selected_provider") String selectedProvider,
            @RequestParam int page,
            @RequestParam("limit") int limits
    ) {
        // Lưu ý: page bắt đầu từ 0 (phải lấy page - 1)
        // page: là trang đang đứng htai, limits: tổng số item trong 1 trang
        PageRequest pageRequest = PageRequest.of(page - 1, limits, Sort.by(Sort.Direction.ASC, "d.price"));
        if(orderBy.equals("desc")){
            pageRequest = PageRequest.of(page - 1, limits, Sort.by(Sort.Direction.DESC, "d.price"));
        }

        // Lấy các products được filter
        Page<ProductResponse> productPage = productService
                .getAllProducts(keyword, categoryId, size, selectedPriceRate, selectedProvider, pageRequest);
        List<ProductResponse> products = productPage.getContent();

        // Lấy số trang của product được filter
        Long totalDistinctCount = productRepository.countDistinctProducts(categoryId, keyword, size, selectedPriceRate, selectedProvider);
        int pageSize = pageRequest.getPageSize();
        int totalPages = (int) Math.ceil((double) totalDistinctCount / pageSize);


        return ResponseEntity.ok(new ProductListResponse().builder()
                    .products(products)
                    .totalPage(totalPages)
                    .build());
    }

    @GetMapping("/get-all-available-sizes")
    public ResponseEntity<?> getAllAvailableSizes(){
        return ResponseEntity.ok().body(ProductResponse.builder()
                .sizes(productService.getAllAvailableSizes())
                .build());
    }



    @GetMapping("/by-ids")
    public ResponseEntity<?> getProductByIds(@RequestParam("ids") String ids){
        // Lấy ra chuỗi productIds có dạng: 1,4,5,6,7 (tách String thành từng con số)
        try {
            // Tách chuỗi id thành 1 List<Long>
            List<Long> productIds = Arrays.stream(ids.split(","))
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
            List<Product> products = productService.findProductByIds(productIds);

            List<ProductResponse> productResponses = new ArrayList<>();

            for(Product product : products){
                ProductResponse productResponse = ProductResponse.fromProduct(product);
                productResponses.add(productResponse);
            }

            return ResponseEntity.ok(productResponses);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }




    @DeleteMapping("/")
    public ResponseEntity<?> deleteProduct(@RequestBody Map<String, Long[]> request) {
        try{
            Long[] ids = request.get("ids");
            productService.deleteProduct(ids);
            return ResponseEntity.ok().body(CategoryResponse.builder()
                            .message("Xóa thành công")
                    .build());
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/generateFakeProducts") // Fake dữ liệu sản phẩm
    public ResponseEntity<String> generatedFakeProducts(){
        Faker faker = new Faker();
        for(int i = 0; i < 1_000; i++){
            String productName = faker.commerce().productName();
            // Nếu tên đã có r bỏ qua, sang vòng lặp tiếp theo
            if(productService.existsByName(productName)) continue;
            ProductDTO productDTO = ProductDTO.builder()
                    .name(productName)
                    .price((float) faker.number().numberBetween(10, 90_000_000))
                    .description(faker.lorem().sentence())
                    .categoryId((long) faker.number().numberBetween(1,4))
                    .build();
            try{
                productService.createProduct(productDTO);
            }catch (Exception e){
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }
        return ResponseEntity.ok("Fake products created succesfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable("id") Long productId) throws Exception{
        try{
            Product product = productService.getProductById(productId);
            ProductResponse productResponse = ProductResponse.fromProduct(product);
            return ResponseEntity.ok().body(productResponse);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/best-sellers")
    public ResponseEntity<?> getAllBestSellers(){
        return ResponseEntity.ok(productService.getAllBestSellers());
    }

    @GetMapping("/new-products")
    public ResponseEntity<?> getNewProducts(){
        return ResponseEntity.ok(productService.getNewProducts());
    }

    @GetMapping("/products-from-category/{category_id}")
    public ResponseEntity<?> getProductByCategory(
            @PathVariable(name = "category_id") Long category_id
    ){
        return ResponseEntity.ok(productService.getProductsByCategory(category_id));
    }

    @GetMapping("/products-from-brand/{brand}")
    public ResponseEntity<?> getProductByBrands(
            @PathVariable(name = "brand") String brand
    ){
        return ResponseEntity.ok(productService.getProductsByBrands(brand));
    }

    private final OrderRepository orderRepository;

//    private RegisterServicesRepository registerServicesRepository;

    @GetMapping("/payment-callback")
    public void paymentCallback(@RequestParam Map<String, String> queryParams, HttpServletResponse response) throws DataNotFoundException, IOException {
        String vnp_ResponseCode = queryParams.get("vnp_ResponseCode");
        String orderId = queryParams.get("userId");
        Long newOrderId = Long.valueOf(orderId);

//        String registerServiceId = queryParams.get("registerServiceId");
        if(orderId!= null && !orderId.equals("")) {
            if ("00".equals(vnp_ResponseCode)) {
                // Giao dịch thành công
                /// Gửi email sau khi order
                Order order = orderRepository.findById(newOrderId)
                        .orElseThrow(() -> new DataNotFoundException("Ko tìm thấy order này!"));
                String emailContent = EmailUtils.getEmailContent(order, order.getOrderDetails());

                String[] recipients = {order.getEmail(), "quangtrinhhuynh02@gmail.com"};

                EmailUtils.sendEmail(recipients, "Twinkle | Đơn hàng của bạn đã được đặt thành công và đang xử lý!", emailContent);
//                response.sendRedirect("http://localhost:4200/order-detail");
                response.sendRedirect("https://twinklee.netlify.app/order-detail");

            } else {
                // Giao dịch thất bại
                // Thực hiện các xử lý cần thiết, ví dụ: không cập nhật CSDL\
                response.sendRedirect("http://localhost:4200/order-detail");
            }
        }
    }



}
