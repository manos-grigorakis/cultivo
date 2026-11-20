package com.mgrigorakis.cultivo.seedbeds.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public record SeedbedSowingRequest(
        @NotNull(message = "Seed count is required")
        @Positive
        Integer seedCount,

        @PositiveOrZero
        Integer germinatedCount,

        @PositiveOrZero
        Integer transplantedCount,

        String notes,

        @NotNull(message = "Sown at is required")
        @PastOrPresent(message = "Sown at cannot be in the future")
        LocalDateTime sownAt,

        @NotNull(message = "Seed packet ID is required")
        Long seedPacketId
) {
    @AssertTrue(message = "Germinated count cannot exceed seed count")
    @JsonIgnore
    public boolean isValidGerminatedCount() {
        return seedCount == null || germinatedCount == null || germinatedCount <= seedCount;
    }

    @AssertTrue(message = "Transplanted count cannot exceed germinated count")
    @JsonIgnore
    public boolean isValidTransplantedCount() {
        if(transplantedCount == null) return true;

        return germinatedCount != null && transplantedCount <= germinatedCount;
    }
}
