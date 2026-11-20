package com.mgrigorakis.cultivo.seedbeds.dto;

import com.mgrigorakis.cultivo.seedbeds.model.enums.SeedbedLocationType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record SeedbedDetailResponse(
        Long id,
        String name,
        SeedbedLocationType locationType,
        Integer rowsCount,
        Integer columnsCount,
        BigDecimal lengthCm,
        BigDecimal widthCm,
        BigDecimal soilDepthCm,
        boolean hasEnclosure,
        boolean hasHumidityControl,
        boolean hasAutomaticWatering,
        String notes,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<SeedbedSlotSummaryResponse> seedbedSlotsSummary
) {}
