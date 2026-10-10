package com.mgrigorakis.cultivo.plants.dto;

import com.mgrigorakis.cultivo.plants.model.enums.PlantStatus;
import jakarta.validation.constraints.NotNull;

public record PlantStatusRequest(
        @NotNull(message = "Status is required")
        PlantStatus status
) {
}
