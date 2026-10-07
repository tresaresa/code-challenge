package com.coding.challenge.inventory.domain;

import java.time.LocalDateTime;

public record User(Long id, String userId, String displayName, String role, LocalDateTime createTimestamp) {
}