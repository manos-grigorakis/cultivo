package com.mgrigorakis.cultivo.seeds.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record SeedPacketCreateRequest(
        @NotBlank(message = "Brand is required")
        @Size(max = 150)
        String brand,

        @Size(max = 150)
        String supplier,

        @Size(max = 100)
        String code,

        @PositiveOrZero
        @Digits(integer = 5, fraction = 2)
        BigDecimal heightCm,

        @PositiveOrZero
        @Digits(integer = 7, fraction = 3)
        BigDecimal weightG,

        @PositiveOrZero
        @Digits(integer = 5, fraction = 2)
        BigDecimal plantSpacingCm,

        @PositiveOrZero
        @Digits(integer = 5, fraction = 2)
        BigDecimal rowSpacingCm,

        @PositiveOrZero
        @Digits(integer = 3, fraction = 2)
        BigDecimal sowingDepthCm,

        Integer germinationDays,
        Integer startHarvestDays,
        List<@NotNull @Min(1) @Max(12) Short> sowingOutdoorMonths,
        List<@NotNull @Min(1) @Max(12) Short> sowingSeedbedMonths,
        LocalDateTime purchasedAt,

        @NotNull(message = "Plant variety id is required")
        Long plantVarietyId
) {}
