package com.coding.challenge.inventory.dto.response;

import com.coding.challenge.inventory.dto.CategoryView;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ListCategoriesResponse extends BaseResponse {

    private final List<CategoryView> categories;

    public ListCategoriesResponse(List<CategoryView> categories) {
        super(BaseResponse.SUCCESS);
        this.categories = categories;
    }

    public ListCategoriesResponse(int statusCode, String errorMessage) {
        super(statusCode, errorMessage);
        this.categories = null;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public List<CategoryView> getCategories() {
        return categories;
    }
}