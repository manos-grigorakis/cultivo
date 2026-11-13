package com.mgrigorakis.cultivo.seeds.service;

import com.mgrigorakis.cultivo.catalog.model.PlantVariety;
import com.mgrigorakis.cultivo.catalog.repository.PlantVarietyRepository;
import com.mgrigorakis.cultivo.common.dto.PageFilterRequest;
import com.mgrigorakis.cultivo.common.dto.PageSortRequest;
import com.mgrigorakis.cultivo.common.exceptions.DuplicateEntryException;
import com.mgrigorakis.cultivo.common.exceptions.ResourceNotFoundException;
import com.mgrigorakis.cultivo.seeds.dto.SeedPacketCreateRequest;
import com.mgrigorakis.cultivo.seeds.dto.SeedPacketResponse;
import com.mgrigorakis.cultivo.seeds.dto.SeedPacketUpdateRequest;
import com.mgrigorakis.cultivo.seeds.mapper.SeedPacketMapper;
import com.mgrigorakis.cultivo.seeds.model.SeedPacket;
import com.mgrigorakis.cultivo.seeds.repository.SeedPacketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class SeedPacketServiceImpl implements SeedPacketService {
    private final SeedPacketRepository seedPacketRepository;
    private final PlantVarietyRepository plantVarietyRepository;
    private final SeedPacketMapper seedPacketMapper;

    @Transactional(readOnly = true)
    @Override
    public Page<SeedPacketResponse> getAllSeedPackets(PageFilterRequest filterRequest, PageSortRequest sortRequest) {
        Pageable pageable = PageRequest.of(filterRequest.page(), filterRequest.size(), sortRequest.createSort("brand"));

        return seedPacketRepository.findAll(pageable).map(seedPacketMapper::toResponse);
    }

    @Transactional(readOnly = true)
    @Override
    public SeedPacketResponse getSeedPacketById(Long id) {
        return seedPacketMapper.toResponse(findSeedPacketById(id));
    }

    @Override
    public SeedPacket findSeedPacketById(Long id) {
        return seedPacketRepository.findById(id).orElseThrow(() -> {
            log.warn("Seed packet with id {} does not exist", id);
            return new ResourceNotFoundException("Seed packet with id " + id + " does not exist");
        });
    }

    @Transactional
    @Override
    public SeedPacketResponse createSeedPacket(SeedPacketCreateRequest request) {
        if (seedPacketRepository.existsByPlantVariety_IdAndBrandAndCode(request.plantVarietyId(), request.brand(),
                                                                        request.code())) {
            log.warn("Seed packet already exists for variety {}, brand {}, code {}", request.plantVarietyId(),
                     request.brand(), request.code());
            throw new DuplicateEntryException(
                    "Seed packet with the same brand and code already exists for this plant variety");
        }

        PlantVariety variety = findPlantVarietyById(request.plantVarietyId());

        SeedPacket seedPacket = seedPacketMapper.toEntity(request);
        seedPacket.setPlantVariety(variety);
        SeedPacket saved = seedPacketRepository.save(seedPacket);
        log.info("Seed packet with id {} created", saved.getId());

        return seedPacketMapper.toResponse(saved);
    }

    @Transactional
    @Override
    public SeedPacketResponse updateSeedPacketById(Long id, SeedPacketUpdateRequest request) {
        SeedPacket seedPacket = findSeedPacketById(id);

        if (seedPacketRepository.existsByPlantVariety_IdAndBrandAndCodeAndIdNot(request.plantVarietyId(),
                                                                                request.brand(), request.code(), id)) {
            log.warn("Seed packet already exists for variety {}, brand {}, code {}", request.plantVarietyId(),
                     request.brand(), request.code());
            throw new DuplicateEntryException(
                    "Seed packet with the same brand and code already exists for this plant variety");
        }

        PlantVariety variety = findPlantVarietyById(request.plantVarietyId());

        SeedPacket updatedSeedPacket = seedPacketMapper.toUpdate(seedPacket, request);
        seedPacket.setPlantVariety(variety);
        SeedPacket saved = seedPacketRepository.save(updatedSeedPacket);
        log.info("Seed packet with id {} updated", saved.getId());

        return seedPacketMapper.toResponse(saved);
    }

    @Transactional
    @Override
    public void deleteSeedPacketById(Long id) {
        seedPacketRepository.delete(findSeedPacketById(id));
        log.info("Seed packet with id {} has been deleted", id);
    }

    /**
     * Finds a plant variety by the provided id if exists
     *
     * @param plantVarietyId The ID of plant variety
     * @return The founded plant variety if exists, otherwise {@link ResourceNotFoundException}
     * @throws ResourceNotFoundException when plant variety not found
     */
    private PlantVariety findPlantVarietyById(Long plantVarietyId) {
        return plantVarietyRepository.findById(plantVarietyId).orElseThrow(() -> {
            log.warn("PlantVariety with id {} does not exist", plantVarietyId);
            return new ResourceNotFoundException("PlantVariety with id " + plantVarietyId + " does not exist");
        });
    }
}
