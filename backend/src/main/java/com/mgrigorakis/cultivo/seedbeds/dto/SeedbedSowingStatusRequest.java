package com.mgrigorakis.cultivo.seedbeds.dto;

import com.mgrigorakis.cultivo.seedbeds.model.enums.SeedbedSowingStatus;
import jakarta.validation.constraints.NotNull;

public record SeedbedSowingStatusRequest(
        @NotNull(message = "Status is required")
        SeedbedSowingStatus status
) {}
