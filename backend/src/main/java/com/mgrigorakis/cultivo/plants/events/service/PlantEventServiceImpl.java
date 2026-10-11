package com.mgrigorakis.cultivo.plants.events.service;

import com.mgrigorakis.cultivo.common.dto.PageFilterRequest;
import com.mgrigorakis.cultivo.common.dto.PageSortRequest;
import com.mgrigorakis.cultivo.common.exceptions.ConflictException;
import com.mgrigorakis.cultivo.common.exceptions.ResourceNotFoundException;
import com.mgrigorakis.cultivo.plants.events.dto.PlantEventRequest;
import com.mgrigorakis.cultivo.plants.events.dto.PlantEventResponse;
import com.mgrigorakis.cultivo.plants.events.mapper.PlantEventMapper;
import com.mgrigorakis.cultivo.plants.events.model.PlantEvent;
import com.mgrigorakis.cultivo.plants.events.repository.PlantEventRepository;
import com.mgrigorakis.cultivo.plants.model.Plant;
import com.mgrigorakis.cultivo.plants.repository.PlantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class PlantEventServiceImpl implements PlantEventService {
    private final PlantEventRepository plantEventRepository;
    private final PlantRepository plantRepository;
    private final PlantEventMapper plantEventMapper;

    @Override
    public Page<PlantEventResponse> getAllPlantEventsByPlantId(Long plantId, PageFilterRequest filterRequest,
                                                              PageSortRequest sortRequest) {
        plantRepository.findById(plantId).orElseThrow(() -> {
            log.warn("Plant with id {} not found", plantId);
            return new ResourceNotFoundException("Plant with id " + plantId + " not found");
        });

        Pageable pageable = PageRequest.of(filterRequest.page(), filterRequest.size(), sortRequest.createSort());
        Page<PlantEvent> plantEvents = plantEventRepository.findAllByPlantId(plantId, pageable);

        return plantEvents.map(plantEventMapper::toResponse);
    }

    @Override
    public PlantEventResponse createPlantEvent(Long plantId, PlantEventRequest request) {
        Plant plant = plantRepository.findById(plantId).orElseThrow(() -> {
            log.warn("Plant with id {} not found", plantId);
            return new ResourceNotFoundException("Plant with id " + plantId + " not found");
        });

        if (plant.getArchivedAt() != null) {
            log.warn("Attempted to create plant event for archived plant with id {}", plantId);
            throw new ConflictException("Attempted to create plant event for archived plant with id " + plantId,
                                        "PLANT_ARCHIVED");
        }

        PlantEvent plantEvent = plantEventMapper.toEntity(request);
        plantEvent.setPlant(plant);
        PlantEvent savedPlantEvent = plantEventRepository.save(plantEvent);
        log.info("Created plantEvent with id {} for plant with id {}", savedPlantEvent.getId(), plantId);

        return plantEventMapper.toResponse(savedPlantEvent);
    }
}
