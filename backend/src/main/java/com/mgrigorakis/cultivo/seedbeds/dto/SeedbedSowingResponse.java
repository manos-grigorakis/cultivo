package com.mgrigorakis.cultivo.seedbeds.dto;

import com.mgrigorakis.cultivo.seedbeds.model.enums.SeedbedSowingStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record SeedbedSowingResponse(
        Long id,
        SeedbedSowingStatus status,
        Integer seedCount,
        Integer germinatedCount,
        Integer transplantedCount,
        String notes,
        LocalDateTime sownAt,
        LocalDateTime completedAt,
        LocalDateTime createdAt,
        LocalDate updatedAt,
        SeedbedSlotSummaryResponse seedbedSlot,
        SeedPacketSummaryResponse seedPacket
) {}
