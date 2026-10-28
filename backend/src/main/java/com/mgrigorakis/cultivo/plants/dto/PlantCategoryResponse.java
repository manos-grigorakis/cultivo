package com.mgrigorakis.cultivo.plants.dto;

import java.time.LocalDateTime;

public record PlantCategoryResponse(
        Long id,
        String name,
        String description,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
