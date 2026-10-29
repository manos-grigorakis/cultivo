package com.mgrigorakis.cultivo.plants.dto;

import com.mgrigorakis.cultivo.plants.model.enums.LifeCycle;
import com.mgrigorakis.cultivo.plants.model.enums.LightRequirement;
import com.mgrigorakis.cultivo.plants.model.enums.WaterRequirement;

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
