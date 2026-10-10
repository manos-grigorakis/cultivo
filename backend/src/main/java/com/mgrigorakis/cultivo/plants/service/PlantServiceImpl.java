package com.mgrigorakis.cultivo.plants.service;

import com.mgrigorakis.cultivo.common.dto.PageFilterRequest;
import com.mgrigorakis.cultivo.common.dto.PageSortRequest;
import com.mgrigorakis.cultivo.common.exceptions.ConflictException;
import com.mgrigorakis.cultivo.common.exceptions.ResourceNotFoundException;
import com.mgrigorakis.cultivo.plants.dto.PlantRequest;
import com.mgrigorakis.cultivo.plants.dto.PlantResponse;
import com.mgrigorakis.cultivo.plants.dto.PlantStatusRequest;
import com.mgrigorakis.cultivo.plants.mapper.PlantMapper;
import com.mgrigorakis.cultivo.plants.model.Plant;
import com.mgrigorakis.cultivo.plants.model.enums.PlantStatus;
import com.mgrigorakis.cultivo.plants.repository.PlantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class PlantServiceImpl implements PlantService {
    private final PlantRepository plantRepository;
    private final PlantMapper plantMapper;

    @Override
    public Page<PlantResponse> getAllPlants(PageFilterRequest filterRequest, PageSortRequest sortRequest) {
        Pageable pageable = PageRequest.of(filterRequest.page(), filterRequest.size(), sortRequest.createSort());
        Page<Plant> plants = plantRepository.findAll(pageable);

        return plants.map(plantMapper::toResponse);
    }

    @Override
    public PlantResponse getPlantById(Long id) {
        Plant plant = plantRepository.findById(id).orElseThrow(() -> {
            log.warn("No plant found with id {}", id);
            return new ResourceNotFoundException("Plant with id " + id + " not found");
        });

        return plantMapper.toResponse(plant);
    }

    @Override
    public PlantResponse createPlant(PlantRequest request) {
        Plant plant = plantMapper.toEntity(request);
        Plant savedPlant = plantRepository.save(plant);
        log.info("Created plant with label {}", savedPlant.getLabel());

        return plantMapper.toResponse(savedPlant);
    }

    @Override
    public PlantResponse updatePlantById(Long id, PlantRequest request) {
        Plant plant = plantRepository.findById(id).orElseThrow(() -> {
            log.warn("No plant found with id {}", id);
            return new ResourceNotFoundException("Plant with id " + id + " not found");
        });

        Plant savedPlant = plantMapper.toUpdate(plant, request);
        plantRepository.save(savedPlant);
        log.info("Updated plant with label {}", savedPlant.getLabel());

        return plantMapper.toResponse(savedPlant);
    }

    @Override
    public void updatePlantStatusById(Long id, PlantStatusRequest request) {
        Plant plant = plantRepository.findById(id).orElseThrow(() -> {
            log.warn("No plant found with id {}", id);
            return new ResourceNotFoundException("Plant with id " + id + " not found");
        });

        if(!plant.canTransitionTo(request.status())) {
            log.warn("Plant with id {} can not transition to status {}", id, request.status());
            throw new ConflictException("Plant with id " + id + " can not transition to status " + request.status(),
                                        Map.of(
                                                ("currentStatus"), plant.getStatus(),
                                                ("desiredStatus"), request.status()),
                                        "STATUS_VIOLATION"
            );
        }

        plant.setStatus(request.status());
        plantRepository.save(plant);
        log.info("Updated plant with id {} and to status {}", id, plant.getStatus());
    }
}
