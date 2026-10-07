package com.coding.challenge.inventory.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record CreateUserRequest(
        @NotBlank @Size(max = 100) String userId,
        @Size(max = 200) String displayName
) {
}