package com.coding.challenge.inventory.service;

import org.springframework.stereotype.Component;

@Component
public class CategoryCombinationValidator {

    public CategoryCombinationValidator() {}

    public boolean isForbidden(String categoryName, String subCategoryName) {
        if ("Shoe".equals(subCategoryName) && "Food".equals(categoryName)) {
            return true;
        }
        if ("Cake".equals(subCategoryName) && "Clothes".equals(categoryName)) {
            return true;
        }
        return false;
    }
}