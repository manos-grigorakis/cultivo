package com.mgrigorakis.cultivo.plants.events.dto;

import com.mgrigorakis.cultivo.plants.events.model.enums.PlantEventType;

import java.time.LocalDateTime;

public record PlantEventResponse(
        Long id,
        Long plantId,
        String title,
        String description,
        PlantEventType type,
        LocalDateTime occurredAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
