package com.mgrigorakis.cultivo.seeds.dto;

import com.mgrigorakis.cultivo.seeds.enums.SeedPacketStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record SeedPacketResponse(
        Long id,
        String brand,
        String supplier,
        SeedPacketStatus status,
        String code,
        BigDecimal heightCm,
        BigDecimal weightG,
        BigDecimal plantSpacingCm,
        BigDecimal rowSpacingCm,
        BigDecimal sowingDepthCm,
        Integer germinationDays,
        Integer startHarvestDays,
        List<Short> sowingOutdoorMonths,
        List<Short> sowingSeedbedMonths,
        LocalDateTime purchasedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        PlantVarietySummary plantVariety
) {}
