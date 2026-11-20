package com.mgrigorakis.cultivo.seedbeds.service;

import com.mgrigorakis.cultivo.common.exceptions.ResourceNotFoundException;
import com.mgrigorakis.cultivo.seedbeds.dto.SeedbedSlotSummaryResponse;
import com.mgrigorakis.cultivo.seedbeds.mapper.SeedbedSlotMapper;
import com.mgrigorakis.cultivo.seedbeds.model.Seedbed;
import com.mgrigorakis.cultivo.seedbeds.model.SeedbedSlot;
import com.mgrigorakis.cultivo.seedbeds.repository.SeedbedRepository;
import com.mgrigorakis.cultivo.seedbeds.repository.SeedbedSlotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class SeedbedSlotServiceImpl implements SeedbedSlotService {
    private final SeedbedSlotRepository seedbedSlotRepository;
    private final SeedbedSlotMapper seedbedSlotMapper;

    @Override
    public List<SeedbedSlotSummaryResponse> getAllSeedbedSlotsBySeedbedId(Long seedbedId) {
        return seedbedSlotRepository.findAllBySeedbed_Id(seedbedId).stream()
                .map(seedbedSlotMapper::toSummaryResponse).toList();
    }

    @Override
    public SeedbedSlotSummaryResponse getSeedbedSlotByIdAndSeedbedId(Long id, Long seedbedId) {
        SeedbedSlot slot = seedbedSlotRepository.findByIdAndSeedbed_Id(id, seedbedId).orElseThrow(() -> {
            log.warn("Seedbed slot with id {} not found in seedbed {}", id, seedbedId);
            return new ResourceNotFoundException("Seedbed slot with id " + id + " not found in seedbed " + seedbedId);
        });

        return seedbedSlotMapper.toSummaryResponse(slot);
    }

    @Override
    public List<SeedbedSlotSummaryResponse> createSeedbedSlotsForSeedbed(Seedbed seedBed) {
        List<SeedbedSlot> slots = new ArrayList<>();

        for(int row = 0; row < seedBed.getRowsCount(); row++) {
            for(int col = 0; col < seedBed.getColumnsCount(); col++) {
                slots.add(SeedbedSlot.builder().rowIndex(row).columnIndex(col).seedbed(seedBed).build());
            }
        }

        List<SeedbedSlot> saved = seedbedSlotRepository.saveAll(slots);
        log.info("Seedbed slots created successfully for seedbed with id {} and total slots count {}", seedBed.getId(), saved.size());

        return saved.stream().map(seedbedSlotMapper::toSummaryResponse).toList();
    }

    @Override
    public void deleteAllSeedbedSlotsBySeedbedId(Long seedbedId) {
        seedbedSlotRepository.deleteAllBySeedbed_Id(seedbedId);
        log.info("Seedbed slots deleted successfully for seedbed with id {}", seedbedId);
    }
}
