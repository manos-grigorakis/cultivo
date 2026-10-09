package com.mgrigorakis.cultivo.plants.dto;

import com.mgrigorakis.cultivo.plants.model.enums.PlantStatus;

import java.time.LocalDateTime;

public record PlantResponse(
        Long id,
        String label,
        PlantStatus status,
        LocalDateTime archivedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
