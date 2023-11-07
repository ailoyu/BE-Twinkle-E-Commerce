package com.twinkle.shopapp.controllers;

import com.twinkle.shopapp.component.LocalizationUtils;
import com.twinkle.shopapp.dtos.CategoryDTO;
import com.twinkle.shopapp.models.Category;
import com.twinkle.shopapp.responses.CategoryResponse;
import com.twinkle.shopapp.responses.UpdateCategoryResponse;
import com.twinkle.shopapp.services.ICategoryService;
import com.twinkle.shopapp.utils.MessageKeys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final ICategoryService categoryService;

    private final LocalizationUtils localizationUtils;


    @PostMapping("")
    public ResponseEntity<?> createCategory(
            @Valid @RequestBody CategoryDTO categoryDTO,
            BindingResult result){
        if(result.hasErrors()){
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(fieldError -> fieldError.getDefaultMessage())
                    .toList();
            return ResponseEntity.badRequest().body(CategoryResponse.builder()
                            .errors(errorMessages)
                            .message(localizationUtils.getLocalizedMessage(MessageKeys.INSERT_CATEGORY_FAILED))
                    .build());
        }

        Category category = categoryService.createCategory(categoryDTO);
        return ResponseEntity.ok().body(CategoryResponse.builder()
                        .message(localizationUtils.getLocalizedMessage(MessageKeys.INSERT_CATEGORY_SUCCESSFULLY))
                        .category(category)
                .build());
    }

    @GetMapping("")
    public ResponseEntity<List<Category>> getAllCategories(){
        List<Category> allCategories = categoryService.getAllCategories();
        return ResponseEntity.ok(allCategories);
    }


    @PutMapping("/{id}")
    public ResponseEntity<UpdateCategoryResponse> updateCategory(
            @PathVariable Long id,
            @RequestBody @Valid CategoryDTO categoryDTO,
            HttpServletRequest request){
        Category updatedCategory = categoryService.updateCategory(id, categoryDTO);


        return ResponseEntity.ok().body(UpdateCategoryResponse.builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.UPDATE_CATEGORY_SUCCESSFULLY))
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id){
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(localizationUtils.getLocalizedMessage(MessageKeys.DELETE_CATEGORY_SUCCESSFULLY));
    }

}
