package com.coding.challenge.inventory.service;

import com.coding.challenge.inventory.domain.Category;
import com.coding.challenge.inventory.dto.CategoryView;
import com.coding.challenge.inventory.dto.request.CreateCategoryRequest;
import com.coding.challenge.inventory.dto.request.DeleteCategoryRequest;
import com.coding.challenge.inventory.dto.request.UpdateCategoryRequest;
import com.coding.challenge.inventory.dto.response.BaseResponse;
import com.coding.challenge.inventory.dto.response.ListCategoriesResponse;
import com.coding.challenge.inventory.dto.response.SimpleOperationResponse;
import com.coding.challenge.inventory.repository.CategoryRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CategoryService {

    private static final Logger log = LoggerFactory.getLogger(CategoryService.class);

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public SimpleOperationResponse createCategory(CreateCategoryRequest request) {
        if (categoryRepository.existsByName(request.name())) {
            return new SimpleOperationResponse(BaseResponse.DUPLICATED, "category name duplicated");
        }
        try {
            categoryRepository.insert(request.name(), request.superCategoryId(), request.userId(), LocalDateTime.now());
            return new SimpleOperationResponse(BaseResponse.SUCCESS);
        } catch (DataAccessException e) {
            log.error("Failed to create category: name={}, categoryId={}", request.name(), request.superCategoryId(), e);
            return new SimpleOperationResponse(BaseResponse.FAILED, "database error");
        }
    }

    public ListCategoriesResponse listAllRootCategories() {
        List<CategoryView> views = categoryRepository.findAllRoots().stream()
                .map(c -> new CategoryView(c.id(), c.name(), c.superCategoryId()))
                .toList();
        return new ListCategoriesResponse(views);
    }

    public ListCategoriesResponse listSubCategoriesByParent(Long parentId) {
        if (!categoryRepository.existsById(parentId)) {
            return new ListCategoriesResponse(BaseResponse.FAILED, "category not found");
        }
        List<CategoryView> views = categoryRepository.findSubsByParentId(parentId).stream()
                .map(c -> new CategoryView(c.id(), c.name(), c.superCategoryId()))
                .toList();
        return new ListCategoriesResponse(views);
    }

    public SimpleOperationResponse updateCategory(UpdateCategoryRequest request) {
        try {
            Category category = categoryRepository.findById(request.categoryId()).orElse(null);
            if (category == null) {
                return new SimpleOperationResponse(BaseResponse.FAILED, "category not found");
            }
            if (!request.name().equals(category.name()) && categoryRepository.existsByName(request.name())) {
                return new SimpleOperationResponse(BaseResponse.DUPLICATED, "category name duplicated");
            }
            boolean updated = categoryRepository.update(category.id(), request.name());
            return updated ? new SimpleOperationResponse(BaseResponse.SUCCESS)
                    : new SimpleOperationResponse(BaseResponse.FAILED, "update failed");
        } catch (DataAccessException e) {
            log.error("Failed to update category: name={}, superCategoryId={}", request.name(), request.categoryId(), e);
            return new SimpleOperationResponse(BaseResponse.FAILED, "database error");
        }
    }

    public SimpleOperationResponse deleteCategory(DeleteCategoryRequest request) {
        try {
            if (!categoryRepository.existsById(request.categoryId())) {
                return new SimpleOperationResponse(BaseResponse.FAILED, "category not found");
            }
            if (categoryRepository.hasChildren(request.categoryId())) {
                return new SimpleOperationResponse(BaseResponse.FAILED, "category has dependent sub-categories");
            }
            if (categoryRepository.hasInventoriesAsCategory(request.categoryId())
                    || categoryRepository.hasInventoriesAsSubCategory(request.categoryId())) {
                return new SimpleOperationResponse(BaseResponse.FAILED, "category has dependent inventories");
            }
            boolean deleted = categoryRepository.deleteById(request.categoryId());
            return deleted ? new SimpleOperationResponse(BaseResponse.SUCCESS)
                    : new SimpleOperationResponse(BaseResponse.FAILED, "delete failed");
        } catch (DataAccessException e) {
            log.error("Failed to delete category: superCategoryId={}", request.categoryId(), e);
            return new SimpleOperationResponse(BaseResponse.FAILED, "database error");
        }
    }
}