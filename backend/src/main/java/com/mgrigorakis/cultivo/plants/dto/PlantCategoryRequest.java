package com.mgrigorakis.cultivo.plants.dto;

import jakarta.validation.constraints.NotBlank;

public record PlantCategoryRequest(
        @NotBlank(message = "Name is required")
        String name,

        String description
) {}
