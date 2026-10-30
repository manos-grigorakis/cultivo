package com.mgrigorakis.cultivo.catalog.dto;

import com.mgrigorakis.cultivo.catalog.model.enums.LifeCycle;
import com.mgrigorakis.cultivo.catalog.model.enums.LightRequirement;
import com.mgrigorakis.cultivo.catalog.model.enums.WaterRequirement;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PlantSpeciesRequest(
        @NotBlank(message = "Name is required")
        @Size(max = 100)
        String name,

        String scientificName,

        String description,

        LifeCycle lifeCycle,

        LightRequirement lightRequirement,

        WaterRequirement waterRequirement,

        @NotNull
        Long plantCategoryId
) {}
