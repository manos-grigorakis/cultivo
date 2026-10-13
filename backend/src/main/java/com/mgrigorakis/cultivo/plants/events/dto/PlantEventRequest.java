package com.mgrigorakis.cultivo.plants.events.dto;

import com.mgrigorakis.cultivo.plants.events.model.enums.PlantEventType;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public record PlantEventRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 200, message = "Title must not exceed 200 characters")
        String title,

        String description,

        @NotNull(message = "Type is required")
        PlantEventType type,

        LocalDateTime occurredAt
) {
}
