package com.coding.challenge.inventory.dto.response;

import com.coding.challenge.inventory.dto.UserView;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ListUsersResponse extends BaseResponse {

    private final List<UserView> users;

    public ListUsersResponse(List<UserView> users) {
        super(BaseResponse.SUCCESS);
        this.users = users;
    }

    public ListUsersResponse(int statusCode, String errorMessage) {
        super(statusCode, errorMessage);
        this.users = null;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public List<UserView> getUsers() {
        return users;
    }
}