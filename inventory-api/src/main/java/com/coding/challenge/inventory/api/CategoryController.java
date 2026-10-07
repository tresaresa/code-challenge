package com.coding.challenge.inventory.api;

import com.coding.challenge.inventory.dto.request.CreateCategoryRequest;
import com.coding.challenge.inventory.dto.request.CreateSubCategoryRequest;
import com.coding.challenge.inventory.dto.request.DeleteCategoryRequest;
import com.coding.challenge.inventory.dto.request.UpdateCategoryRequest;
import com.coding.challenge.inventory.dto.response.ListCategoriesResponse;
import com.coding.challenge.inventory.dto.response.SimpleOperationResponse;
import com.coding.challenge.inventory.service.CategoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/category")
@Tag(name = "Category")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/create")
    public SimpleOperationResponse createCategory(@Valid @RequestBody CreateCategoryRequest request) {
        return categoryService.createCategory(request);
    }

    @GetMapping("/all-root")
    public ListCategoriesResponse listAllRootCategories() {
        return categoryService.listAllRootCategories();
    }

    @GetMapping("/sub/{parent_id}")
    public ListCategoriesResponse listSubCategories(@PathVariable("parent_id") Long parentId) {
        return categoryService.listSubCategoriesByParent(parentId);
    }

    @PostMapping("/update")
    public SimpleOperationResponse updateCategory(@Valid @RequestBody UpdateCategoryRequest request) {
        return categoryService.updateCategory(request);
    }

    @PostMapping("/delete")
    public SimpleOperationResponse deleteCategory(@Valid @RequestBody DeleteCategoryRequest request) {
        return categoryService.deleteCategory(request);
    }
}