package com.mgrigorakis.cultivo.plants.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PlantRequest(
    @NotBlank(message = "Label is required")
    @Size(max = 150)
    String label
) {
}
