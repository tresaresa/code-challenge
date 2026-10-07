package com.coding.challenge.inventory.domain;

import java.time.LocalDateTime;

public record Inventory(Long id, String name, String description, Long categoryId, 
                        Integer quantity, String createUser, LocalDateTime createTimestamp) {
}