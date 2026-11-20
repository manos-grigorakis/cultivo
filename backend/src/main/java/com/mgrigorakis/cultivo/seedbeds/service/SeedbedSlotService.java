package com.mgrigorakis.cultivo.seedbeds.service;

import com.mgrigorakis.cultivo.seedbeds.dto.SeedbedSlotSummaryResponse;
import com.mgrigorakis.cultivo.seedbeds.model.Seedbed;

import java.util.List;

public interface SeedbedSlotService {
    List<SeedbedSlotSummaryResponse> getAllSeedbedSlotsBySeedbedId(Long seedbedId);

    SeedbedSlotSummaryResponse getSeedbedSlotByIdAndSeedbedId(Long id, Long seedbedId);

    List<SeedbedSlotSummaryResponse> createSeedbedSlotsForSeedbed(Seedbed seedBed);

    void deleteAllSeedbedSlotsBySeedbedId(Long seedbedId);
}
