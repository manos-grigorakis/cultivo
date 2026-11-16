package com.mgrigorakis.cultivo.catalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PlantVarietyRequest(
        @NotBlank(message = "Name is required")
        @Size(min = 3, max = 150)
        String name,

        String description,

        Long plantSpeciesId
) {}
