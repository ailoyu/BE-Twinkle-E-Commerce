package com.twinkle.shopapp.controllers;

import com.twinkle.shopapp.component.LocalizationUtils;
import com.twinkle.shopapp.dtos.ProviderDTO;
import com.twinkle.shopapp.models.Provider;
import com.twinkle.shopapp.responses.*;
import com.twinkle.shopapp.services.IProviderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${api.prefix}/providers")
@RequiredArgsConstructor
public class ProviderController {

    private final IProviderService providerService;

    private final LocalizationUtils localizationUtils;


    @PostMapping("")
    public ResponseEntity<?> createProvider(
            @Valid @RequestBody ProviderDTO providerDTO,
            BindingResult result){
        if(result.hasErrors()){
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(fieldError -> fieldError.getDefaultMessage())
                    .toList();
            return ResponseEntity.badRequest().body(ProviderResponse.builder()
                    .errors(errorMessages)
                    .message("Tạo provider thất bại")
                    .build());
        }

        Provider provider = providerService.createProvider(providerDTO);
        return ResponseEntity.ok().body(ProviderResponse.builder()
                .message("Tạo provider thành công!")
                .provider(provider)
                .build());
    }

    @GetMapping("/get-providers-by-filter")
    public ResponseEntity<ProviderListResponse> getAllProducts(
            @RequestParam(defaultValue = "") String keyword, // search
            @RequestParam(defaultValue = "") String address, // tìm theo thể loại
            @RequestParam int page,
            @RequestParam("limit") int limits
    ) {
        // Lưu ý: page bắt đầu từ 0 (phải lấy page - 1)
        // page: là trang đang đứng htai, limits: tổng số item trong 1 trang
        PageRequest pageRequest = PageRequest.of(
                page - 1, limits,
//                Sort.by("createdAt").descending());
                Sort.by("id").ascending() // sắp xếp theo id tăng dần
        );

        Page<Provider> providerPage = providerService
                .getProvidersByFilter(keyword, address, pageRequest);

        // lấy tổng số trang
        int totalPages = providerPage.getTotalPages();

        // danh sách các products ở tất cả các trang
        List<Provider> providers = providerPage.getContent();

        return ResponseEntity.ok(new ProviderListResponse().builder()
                .providers(providers)
                .totalPage(totalPages)
                .build());
    }

    @GetMapping("")
    public ResponseEntity<List<Provider>> getAllProviders(){
        List<Provider> allProviders = providerService.getAllProviders();
        return ResponseEntity.ok(allProviders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProviderById(@PathVariable("id") Long id){
        Provider providerById = providerService.getProviderById(id);
        return ResponseEntity.ok(ProviderResponse.builder()
                .provider(providerById)
                .message("Oke")
                .build());
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateProvider(
            @PathVariable Long id,
            @RequestBody @Valid ProviderDTO providerDTO,
            BindingResult result){

        if(result.hasErrors()){
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(fieldError -> fieldError.getDefaultMessage())
                    .toList();
            return ResponseEntity.badRequest().body(ProviderResponse.builder()
                    .errors(errorMessages)
                    .message("Cập nhật provider thất bại")
                    .build());
        }


        Provider updatedProvider = providerService.updateProvider(id, providerDTO);


        return ResponseEntity.ok().body(ProviderResponse.builder()
                        .provider(updatedProvider)
                .message("Cập nhật provider thành công")
                .build());
    }

    @DeleteMapping("/")
    public ResponseEntity<?> deleteProvider(@RequestBody Map<String, Long[]> request){
        try{
            Long[] ids = request.get("ids");
            providerService.deleteProvider(ids);
            return ResponseEntity.ok().body(CategoryResponse.builder()
                    .message("Xóa thành công")
                    .build());
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
