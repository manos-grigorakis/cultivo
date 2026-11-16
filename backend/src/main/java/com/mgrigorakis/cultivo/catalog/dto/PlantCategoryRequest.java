package com.mgrigorakis.cultivo.catalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PlantCategoryRequest(
        @NotBlank(message = "Name is required")
        @Size(min = 3, max = 100)
        String name,

        String description
) {}
