package com.coding.challenge.inventory.api;

import com.coding.challenge.inventory.dto.request.CreateInventoryRequest;
import com.coding.challenge.inventory.dto.request.DeleteInventoryRequest;
import com.coding.challenge.inventory.dto.request.UpdateInventoryQuantityRequest;
import com.coding.challenge.inventory.dto.response.InventoryResponse;
import com.coding.challenge.inventory.dto.response.ListInventoriesResponse;
import com.coding.challenge.inventory.dto.response.SimpleOperationResponse;
import com.coding.challenge.inventory.service.InventoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inventory")
@Tag(name = "Inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping("/create")
    public SimpleOperationResponse createInventory(@Valid @RequestBody CreateInventoryRequest request) {
        return inventoryService.createInventory(request);
    }

    @PostMapping("/update-quantity")
    public SimpleOperationResponse updateQuantity(@Valid @RequestBody UpdateInventoryQuantityRequest request) {
        return inventoryService.updateQuantity(request);
    }

    @GetMapping("/all")
    public ListInventoriesResponse listAllInventories() {
        return inventoryService.listAllInventories();
    }

    @GetMapping("/{id}")
    public InventoryResponse getInventory(@PathVariable("id") Long id) {
        return inventoryService.getInventoryById(id);
    }

    @PostMapping("/delete")
    public SimpleOperationResponse deleteInventory(@Valid @RequestBody DeleteInventoryRequest request) {
        return inventoryService.deleteInventory(request);
    }
}