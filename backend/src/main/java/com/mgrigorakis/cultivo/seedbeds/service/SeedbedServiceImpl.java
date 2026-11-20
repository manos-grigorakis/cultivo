package com.mgrigorakis.cultivo.seedbeds.service;

import com.mgrigorakis.cultivo.common.dto.PageFilterRequest;
import com.mgrigorakis.cultivo.common.dto.PageSortRequest;
import com.mgrigorakis.cultivo.common.exceptions.ConflictException;
import com.mgrigorakis.cultivo.common.exceptions.DuplicateEntryException;
import com.mgrigorakis.cultivo.common.exceptions.ResourceNotFoundException;
import com.mgrigorakis.cultivo.seedbeds.dto.*;
import com.mgrigorakis.cultivo.seedbeds.mapper.SeedbedMapper;
import com.mgrigorakis.cultivo.seedbeds.model.Seedbed;
import com.mgrigorakis.cultivo.seedbeds.repository.SeedbedRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class SeedbedServiceImpl implements SeedbedService {
    private final SeedbedRepository seedbedRepository;
    private final SeedbedMapper seedbedMapper;
    private final SeedbedSlotService seedbedSlotService;

    @Override
    public Page<SeedbedResponse> getAllSeedbeds(PageFilterRequest filterRequest, PageSortRequest sortRequest) {
        Pageable pageable = PageRequest.of(filterRequest.page(), filterRequest.size(), sortRequest.createSort("name"));
        return seedbedRepository.findAll(pageable).map(seedbedMapper::toResponse);
    }


    @Transactional(readOnly = true)
    @Override
    public SeedbedDetailResponse getSeedbedById(Long id) {
        Seedbed seedBed = findSeedbedById(id);
        List<SeedbedSlotSummaryResponse> slots = seedbedSlotService.getAllSeedbedSlotsBySeedbedId(id);

        return seedbedMapper.toResponse(seedBed, slots);
    }

    @Override
    public Seedbed findSeedbedById(Long id) {
        return seedbedRepository.findById(id).orElseThrow(() -> {
            log.warn("Seedbed not found with id {}", id);
            return new ResourceNotFoundException("Seedbed not found with id " + id);
        });
    }

    @Transactional
    @Override
    public SeedbedDetailResponse createSeedbed(SeedbedCreateRequest request) {
        if (seedbedRepository.existsByNameIgnoreCase(request.name())) {
            log.warn("Seedbed already exists with name {}", request.name());
            throw new DuplicateEntryException("Seedbed already exists with name " + request.name());
        }

        Seedbed seedbed = seedbedMapper.toEntity(request);
        Seedbed saved = seedbedRepository.save(seedbed);

        List<SeedbedSlotSummaryResponse> slotsSummary = seedbedSlotService.createSeedbedSlotsForSeedbed(saved);

        log.info("Seedbed created with id {} and name {}", saved.getId(), saved.getName());

        return seedbedMapper.toResponse(saved, slotsSummary);
    }

    @Override
    public SeedbedResponse updateSeedbedById(Long id, SeedbedUpdateRequest request) {
        Seedbed seedBed = findSeedbedById(id);

        if (seedbedRepository.existsByNameIgnoreCaseAndIdNot(request.name(), id)) {
            log.warn("Seedbed already exists with name {}", request.name());
            throw new DuplicateEntryException("Seedbed already exists with name " + request.name());
        }

        Seedbed updated = seedbedMapper.toUpdate(seedBed, request);
        Seedbed saved = seedbedRepository.save(updated);
        log.info("Seedbed updated with id {}", saved.getId());

        return seedbedMapper.toResponse(saved);
    }

    @Transactional
    @Override
    public void deleteSeedbedById(Long id) {
        Seedbed seedbed = findSeedbedById(id);

        seedbedSlotService.deleteAllSeedbedSlotsBySeedbedId(id);
        seedbedRepository.delete(seedbed);
        log.info("Seedbed with id {} deleted", id);
    }
}
