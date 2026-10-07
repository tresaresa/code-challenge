package com.coding.challenge.inventory.domain;

import java.time.LocalDateTime;

public record Category(Long id, String name, Long superCategoryId, String createUser,
                       LocalDateTime createTimestamp) {
}