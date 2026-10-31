package com.mgrigorakis.cultivo.catalog.dto;

import java.time.LocalDateTime;

public record PlantVarietyResponse(
        Long id,
        String name,
        String description,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long plantSpeciesId
) {}
