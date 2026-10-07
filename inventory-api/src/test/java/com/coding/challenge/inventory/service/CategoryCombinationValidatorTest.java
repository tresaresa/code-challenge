package com.coding.challenge.inventory.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CategoryCombinationValidatorTest {

    @Test
    void forbiddenCombinationReturnsTrue() {
        CategoryCombinationValidator validator = new CategoryCombinationValidator();
        assertTrue(validator.isForbidden("Food", "Shoe"));
        assertTrue(validator.isForbidden("Clothes", "Cake"));
    }

    @Test
    void allowedCombinationReturnsFalse() {
        CategoryCombinationValidator validator = new CategoryCombinationValidator();
        assertFalse(validator.isForbidden("Clothes", "Shoe"));
        assertFalse(validator.isForbidden("Food", "Cake"));
    }
}