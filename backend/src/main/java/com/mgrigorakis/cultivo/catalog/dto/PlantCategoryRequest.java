package com.mgrigorakis.cultivo.catalog.dto;

import jakarta.validation.constraints.NotBlank;

public record PlantCategoryRequest(
        @NotBlank(message = "Name is required")
        String name,

        String description
) {}
