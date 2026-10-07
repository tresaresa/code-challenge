package com.coding.challenge.inventory.dto.response;

import com.coding.challenge.inventory.dto.InventoryView;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class InventoryResponse extends BaseResponse {

    private final InventoryView inventory;

    public InventoryResponse(InventoryView inventory) {
        super(BaseResponse.SUCCESS);
        this.inventory = inventory;
    }

    public InventoryResponse(int statusCode, String errorMessage) {
        super(statusCode, errorMessage);
        this.inventory = null;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public InventoryView getInventory() {
        return inventory;
    }
}