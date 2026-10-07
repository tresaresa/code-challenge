package com.coding.challenge.inventory.service;

import com.coding.challenge.inventory.domain.Category;
import com.coding.challenge.inventory.domain.Inventory;
import com.coding.challenge.inventory.dto.InventoryView;
import com.coding.challenge.inventory.dto.request.CreateInventoryRequest;
import com.coding.challenge.inventory.dto.request.DeleteInventoryRequest;
import com.coding.challenge.inventory.dto.request.UpdateInventoryQuantityRequest;
import com.coding.challenge.inventory.dto.response.BaseResponse;
import com.coding.challenge.inventory.dto.response.InventoryResponse;
import com.coding.challenge.inventory.dto.response.ListInventoriesResponse;
import com.coding.challenge.inventory.dto.response.SimpleOperationResponse;
import com.coding.challenge.inventory.repository.CategoryRepository;
import com.coding.challenge.inventory.repository.InventoryRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class InventoryService {

    private static final Logger log = LoggerFactory.getLogger(InventoryService.class);

    private final InventoryRepository inventoryRepository;
    private final CategoryRepository categoryRepository;

    public InventoryService(InventoryRepository inventoryRepository,
                            CategoryRepository categoryRepository) {
        this.inventoryRepository = inventoryRepository;
        this.categoryRepository = categoryRepository;
    }

    public SimpleOperationResponse createInventory(CreateInventoryRequest request) {
        Category category = categoryRepository.findById(request.categoryId()).orElse(null);
        if (category == null || category.superCategoryId() != null) {
            return new SimpleOperationResponse(BaseResponse.FAILED, "category not found");
        }
        if (inventoryRepository.existsByName(request.name())) {
            return new SimpleOperationResponse(BaseResponse.DUPLICATED, "inventory name duplicated");
        }
        try {
            inventoryRepository.insert(request.name(), request.description(), request.categoryId(), request.categoryId(), 0, request.userId(), LocalDateTime.now());
            return new SimpleOperationResponse(BaseResponse.SUCCESS);
        } catch (DataAccessException e) {
            log.error("Failed to create inventory: name={}, inventoryId={}", request.name(), request.categoryId(), e);
            return new SimpleOperationResponse(BaseResponse.FAILED, "database error");
        }
    }

    public SimpleOperationResponse updateQuantity(UpdateInventoryQuantityRequest request) {
        if (!inventoryRepository.existsById(request.inventoryId())) {
            return new SimpleOperationResponse(BaseResponse.FAILED, "inventory not found");
        }
        try {
            boolean updated = inventoryRepository.updateQuantity(request.inventoryId(), request.quantity());
            if (updated) {
                log.info("Quantity updated: inventoryId={}, quantity={}, user={}",
                        request.inventoryId(), request.quantity(), request.userId());
                return new SimpleOperationResponse(BaseResponse.SUCCESS);
            }
            return new SimpleOperationResponse(BaseResponse.FAILED, "update failed");
        } catch (DataAccessException e) {
            log.error("Failed to update inventory: inventoryId={}", request.inventoryId(), e);
            return new SimpleOperationResponse(BaseResponse.FAILED, "database error");
        }
    }

    public ListInventoriesResponse listAllInventories() {
        List<InventoryView> views = inventoryRepository.findAll().stream()
                .map(this::toView)
                .toList();
        return new ListInventoriesResponse(views);
    }

    public InventoryResponse getInventoryById(Long id) {
        try {
            return inventoryRepository.findById(id)
                    .map(i -> new InventoryResponse(toView(i)))
                    .orElseGet(() -> new InventoryResponse(BaseResponse.FAILED, "inventory not found"));
        } catch (DataAccessException e) {
            log.error("Failed to create inventory: inventoryId={}", id, e);
            return new InventoryResponse(BaseResponse.FAILED, "database error");
        }
    }

    public SimpleOperationResponse deleteInventory(DeleteInventoryRequest request) {
        if (!inventoryRepository.existsById(request.inventoryId())) {
            return new SimpleOperationResponse(BaseResponse.FAILED, "inventory not found");
        }
        try {
            boolean deleted = inventoryRepository.deleteById(request.inventoryId());
            if (deleted) {
                return new SimpleOperationResponse(BaseResponse.SUCCESS);
            }
            return new SimpleOperationResponse(BaseResponse.FAILED, "delete failed");
        } catch (DataAccessException e) {
            log.error("Failed to delete inventory: inventoryId={}", request.inventoryId(), e);
            return new SimpleOperationResponse(BaseResponse.FAILED, "database error");
        }
    }

    private InventoryView toView(Inventory inventory) {
        String categoryName = categoryRepository.findById(inventory.categoryId())
                .map(Category::name)
                .orElse(null);
        return new InventoryView(inventory.id(), inventory.name(), inventory.description(),
                categoryName, inventory.quantity());
    }
}