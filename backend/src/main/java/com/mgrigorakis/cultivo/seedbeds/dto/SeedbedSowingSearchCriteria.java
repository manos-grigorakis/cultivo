package com.mgrigorakis.cultivo.seedbeds.dto;

import com.mgrigorakis.cultivo.seedbeds.model.enums.SeedbedSowingStatus;

public record SeedbedSowingSearchCriteria(
        Long seedbedId,
        Long slotId,
        Long packetId,
        SeedbedSowingStatus status
) {}
