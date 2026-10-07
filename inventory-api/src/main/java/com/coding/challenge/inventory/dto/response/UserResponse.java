package com.coding.challenge.inventory.dto.response;

import com.coding.challenge.inventory.dto.UserView;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserResponse extends BaseResponse {

    private final UserView user;

    public UserResponse(UserView user) {
        super(BaseResponse.SUCCESS);
        this.user = user;
    }

    public UserResponse(int statusCode, String errorMessage) {
        super(statusCode, errorMessage);
        this.user = null;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public UserView getUser() {
        return user;
    }
}