package com.mgrigorakis.cultivo.plants.service;

import com.mgrigorakis.cultivo.common.exceptions.ResourceNotFoundException;
import com.mgrigorakis.cultivo.plants.dto.PlantRequest;
import com.mgrigorakis.cultivo.plants.dto.PlantResponse;
import com.mgrigorakis.cultivo.plants.mapper.PlantMapper;
import com.mgrigorakis.cultivo.plants.model.Plant;
import com.mgrigorakis.cultivo.plants.repository.PlantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class PlantServiceImpl implements PlantService {
    private final PlantRepository plantRepository;
    private final PlantMapper plantMapper;

    @Override
    public List<PlantResponse> getAllPlants() {
        return plantRepository.findAll().stream().map(plantMapper::toResponse).toList();
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
}
