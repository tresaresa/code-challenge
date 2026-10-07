package com.coding.challenge.inventory.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

public abstract class BaseResponse {

    public static final int SUCCESS = 0;
    public static final int DUPLICATED = 1;
    public static final int FAILED = 2;

    protected final int statusCode;
    protected final String errorMessage;

    protected BaseResponse(int statusCode) {
        this(statusCode, null);
    }

    protected BaseResponse(int statusCode, String errorMessage) {
        this.statusCode = statusCode;
        this.errorMessage = errorMessage;
    }

    public int getStatusCode() {
        return statusCode;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getErrorMessage() {
        return errorMessage;
    }
}