package com.mgrigorakis.cultivo.catalog.dto;

import com.mgrigorakis.cultivo.catalog.model.enums.LifeCycle;
import com.mgrigorakis.cultivo.catalog.model.enums.LightRequirement;
import com.mgrigorakis.cultivo.catalog.model.enums.WaterRequirement;

import java.time.LocalDateTime;

public record PlantSpeciesResponse(
        Long id,
        String name,
        String scientificName,
        String description,
        LifeCycle lifeCycle,
        LightRequirement lightRequirement,
        WaterRequirement waterRequirement,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        PlantCategorySummary plantCategory
) {}
