package com.coding.challenge.inventory.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SimpleOperationResponse extends BaseResponse {

    public SimpleOperationResponse(int statusCode) {
        super(statusCode);
    }

    public SimpleOperationResponse(int statusCode, String errorMessage) {
        super(statusCode, errorMessage);
    }
}