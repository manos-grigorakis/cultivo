package com.mgrigorakis.cultivo.seedbeds.service;

import com.mgrigorakis.cultivo.common.dto.PageFilterRequest;
import com.mgrigorakis.cultivo.common.dto.PageSortRequest;
import com.mgrigorakis.cultivo.common.exceptions.ConflictException;
import com.mgrigorakis.cultivo.common.exceptions.ResourceNotFoundException;
import com.mgrigorakis.cultivo.seedbeds.dto.SeedbedSowingRequest;
import com.mgrigorakis.cultivo.seedbeds.dto.SeedbedSowingResponse;
import com.mgrigorakis.cultivo.seedbeds.dto.SeedbedSowingSearchCriteria;
import com.mgrigorakis.cultivo.seedbeds.dto.SeedbedSowingStatusRequest;
import com.mgrigorakis.cultivo.seedbeds.mapper.SeedbedSowingMapper;
import com.mgrigorakis.cultivo.seedbeds.model.SeedbedSlot;
import com.mgrigorakis.cultivo.seedbeds.model.SeedbedSowing;
import com.mgrigorakis.cultivo.seedbeds.model.enums.SeedbedSowingStatus;
import com.mgrigorakis.cultivo.seedbeds.repository.SeedbedSlotRepository;
import com.mgrigorakis.cultivo.seedbeds.repository.SeedbedSowingRepository;
import com.mgrigorakis.cultivo.seedbeds.specs.SeedbedSowingSpecs;
import com.mgrigorakis.cultivo.seeds.model.SeedPacket;
import com.mgrigorakis.cultivo.seeds.service.SeedPacketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class SeedbedSowingServiceImpl implements SeedbedSowingService {
    private final SeedbedSowingRepository seedbedSowingRepository;
    private final SeedbedSlotRepository seedbedSlotRepository;
    private final SeedbedSowingMapper seedbedSowingMapper;

    private final SeedPacketService seedPacketService;

    @Transactional(readOnly = true)
    @Override
    public Page<SeedbedSowingResponse> getAllSeedbedSowings(PageFilterRequest filterRequest,
                                                            PageSortRequest sortRequest,
                                                            SeedbedSowingSearchCriteria criteria) {
        Pageable pageable = PageRequest.of(
                filterRequest.page(),
                filterRequest.size(),
                sortRequest.createSort("sownAt", Sort.Direction.DESC));

        Specification<SeedbedSowing> spec = Specification.allOf(
                SeedbedSowingSpecs.hasSeedbedId(criteria.seedbedId()),
                SeedbedSowingSpecs.hasSlotId(criteria.slotId()),
                SeedbedSowingSpecs.hasPacketId(criteria.packetId()),
                SeedbedSowingSpecs.hasStatus(criteria.status())
        );

        return seedbedSowingRepository.findAll(spec, pageable).map(seedbedSowingMapper::toResponse);
    }

    @Transactional(readOnly = true)
    @Override
    public SeedbedSowingResponse getSeedbedSowingByIdAndSeedbedIdAndSlotId(Long id, Long seedbedId, Long slotId) {
        return seedbedSowingMapper.toResponse(findSeedbedSowingByIdAndSlotIdAndSeedbedId(id, slotId, seedbedId));
    }

    @Transactional(readOnly = true)
    @Override
    public SeedbedSowingResponse getSeedbedSowingById(Long id) {
        return seedbedSowingMapper.toResponse(findSeedbedSowingById(id));
    }

    @Override
    public SeedbedSowing findSeedbedSowingById(Long id) {
        return seedbedSowingRepository.findById(id).orElseThrow(() -> {
            log.warn("Seedbed sowing not found with id {}", id);
            return new ResourceNotFoundException("Seedbed sowing not found with id " + id);
        });
    }

    @Transactional
    @Override
    public SeedbedSowingResponse createSeedbedSowing(Long seedbedId, Long slotId, SeedbedSowingRequest request) {
        SeedbedSlot slot = seedbedSlotRepository.findByIdAndSeedbed_Id(slotId, seedbedId).orElseThrow(() -> {
            log.warn("Seedbed slot not found with id {} for seedbed id {}", slotId, seedbedId);
            return new ResourceNotFoundException(
                    "Seedbed slot not found with id " + slotId + " for seedbed id " + seedbedId);
        });

        SeedPacket packet = seedPacketService.findSeedPacketById(request.seedPacketId());

        boolean hasActiveSowing = seedbedSowingRepository.existsBySlot_IdAndStatusIn(slotId, List.of(
                SeedbedSowingStatus.SOWN, SeedbedSowingStatus.GERMINATING, SeedbedSowingStatus.GERMINATED));

        if (hasActiveSowing) {
            log.warn("Seedbed slot with id {} is already contains active sowing for seedbed with id {}", slotId,
                     seedbedId);
            throw new ConflictException("Seedbed slot already contains an active sowing", "ACTIVE_SOWING_EXISTS");
        }

        SeedbedSowing sowing = seedbedSowingMapper.toEntity(request);
        sowing.setSlot(slot);
        sowing.setPacket(packet);

        SeedbedSowing saved = seedbedSowingRepository.save(sowing);
        log.info("Seedbed sowing created with id {}", saved.getId());

        return seedbedSowingMapper.toResponse(saved);
    }

    @Transactional
    @Override
    public SeedbedSowingResponse updateSeedbedSowingById(Long id, Long seedbedId, Long slotId,
                                                         SeedbedSowingRequest request) {
        SeedbedSowing sowing = findSeedbedSowingByIdAndSlotIdAndSeedbedId(id, slotId, seedbedId);

        if (sowing.isTerminalStatus()) {
            log.warn("Seedbed sowing with id {} cannot modified because its status {} is terminal", sowing.getId(),
                     sowing.getStatus());
            throw new ConflictException(
                    "Seedbed sowing with id " + id + " cannot be modified because its status " + sowing.getStatus() + " is terminal",
                    "SOWING_TERMINAL_STATUS");
        }

        SeedPacket packet = seedPacketService.findSeedPacketById(request.seedPacketId());

        SeedbedSowing updated = seedbedSowingMapper.toUpdate(sowing, request);
        updated.setPacket(packet);
        SeedbedSowing saved = seedbedSowingRepository.save(updated);
        log.info("Seedbed sowing updated with id {}", saved.getId());

        return seedbedSowingMapper.toResponse(saved);
    }

    @Transactional
    @Override
    public void updateSeedbedSowingStatusById(Long id, Long seedbedId, Long slotId,
                                              SeedbedSowingStatusRequest request) {
        SeedbedSowing sowing = findSeedbedSowingByIdAndSlotIdAndSeedbedId(id, slotId, seedbedId);

        if (!sowing.canTransitionTo(request.status())) {
            log.warn("Sowing with {} cannot transition to {} for seedbed id {} due to transition violation", id,
                     request.status(),
                     seedbedId);
            throw new ConflictException(
                    "Sowing with id " + id + " cannot transition to " + request.status() + " for seedbed id " + seedbedId,
                    "STATUS_TRANSITION_VIOLATION");
        }

        sowing.setStatus(request.status());

        if (sowing.isTerminalStatus()) sowing.setCompletedAt(LocalDateTime.now());

        SeedbedSowing saved = seedbedSowingRepository.save(sowing);
        log.info("Seedbed status updated with id {}", saved.getId());
    }

    @Override
    public void deleteSeedbedSowingById(Long id) {
        SeedbedSowing sowing = findSeedbedSowingById(id);

        if(!sowing.getStatus().equals(SeedbedSowingStatus.SOWN)) {
            log.warn("Sowing with id {} cannot be deleted because its status is {}", id, sowing.getStatus());
            throw new ConflictException(
                    "Seedbed sowing with id " + id + " cannot be deleted because only sowings with SOWN status can be deleted",
                    "SOWING_DELETION_NOT_ALLOWED");
        }

        seedbedSowingRepository.delete(sowing);
        log.info("Seedbed status deleted with id {}", id);
    }

    /**
     * Finds a seedbed sowing by the provided arguments if exists
     *
     * @param id        The seedbed sowing ID to search for
     * @param slotId    The seedbed slot ID to search for
     * @param seedbedId The seedbed ID to search for
     * @return The founded seedbed sowing if exists
     * @throws ResourceNotFoundException when seedbed sowing does not exist
     */
    private SeedbedSowing findSeedbedSowingByIdAndSlotIdAndSeedbedId(Long id, Long slotId, Long seedbedId) {
        return seedbedSowingRepository.findByIdAndSlot_IdAndSlot_Seedbed_Id(id, slotId, seedbedId)
                .orElseThrow(() -> {
                    log.warn("Seedbed sowing not found with id {} and slot id {} and seedbed id {}", id, slotId,
                             seedbedId);
                    return new ResourceNotFoundException(
                            "Seedbed sowing not found with the provided combination of parameters");
                });
    }
}
