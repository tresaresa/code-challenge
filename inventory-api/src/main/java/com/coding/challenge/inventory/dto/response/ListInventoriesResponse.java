package com.coding.challenge.inventory.dto.response;

import com.coding.challenge.inventory.dto.InventoryView;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ListInventoriesResponse extends BaseResponse {

    private final List<InventoryView> inventories;

    public ListInventoriesResponse(List<InventoryView> inventories) {
        super(BaseResponse.SUCCESS);
        this.inventories = inventories;
    }

    public ListInventoriesResponse(int statusCode, String errorMessage) {
        super(statusCode, errorMessage);
        this.inventories = null;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public List<InventoryView> getInventories() {
        return inventories;
    }
}