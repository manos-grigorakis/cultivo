package com.mgrigorakis.cultivo.seedbeds.dto;

import com.mgrigorakis.cultivo.seedbeds.model.enums.SeedbedLocationType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record SeedbedCreateRequest(
        @NotBlank(message = "Name is required")
        @Size(min = 3, max = 100)
        String name,

        @NotNull(message = "Location type is required")
        SeedbedLocationType locationType,

        @NotNull(message = "Rows count is required")
        @Positive
        Integer rowsCount,

        @NotNull(message = "Columns count is required")
        @Positive
        Integer columnsCount,

        @PositiveOrZero
        @Digits(integer = 6, fraction = 2)
        BigDecimal lengthCm,

        @PositiveOrZero
        @Digits(integer = 6, fraction = 2)
        BigDecimal widthCm,

        @PositiveOrZero
        @Digits(integer = 5, fraction = 2)
        BigDecimal soilDepthCm,

        @NotNull(message = "Enclosure is required")
        Boolean hasEnclosure,

        @NotNull(message = "Humidity control is required")
        Boolean hasHumidityControl,

        @NotNull(message = "Automatic watering is required")
        Boolean hasAutomaticWatering,

        String notes
) {}
