package com.mgrigorakis.cultivo.plants.service;

import com.mgrigorakis.cultivo.catalog.model.PlantSpecies;
import com.mgrigorakis.cultivo.catalog.model.PlantVariety;
import com.mgrigorakis.cultivo.catalog.repository.PlantSpeciesRepository;
import com.mgrigorakis.cultivo.catalog.repository.PlantVarietyRepository;
import com.mgrigorakis.cultivo.common.dto.PageFilterRequest;
import com.mgrigorakis.cultivo.common.dto.PageSortRequest;
import com.mgrigorakis.cultivo.common.exceptions.ConflictException;
import com.mgrigorakis.cultivo.common.exceptions.ResourceNotFoundException;
import com.mgrigorakis.cultivo.plants.dto.PlantRequest;
import com.mgrigorakis.cultivo.plants.dto.PlantResponse;
import com.mgrigorakis.cultivo.plants.dto.PlantStatusRequest;
import com.mgrigorakis.cultivo.plants.mapper.PlantMapper;
import com.mgrigorakis.cultivo.plants.model.Plant;
import com.mgrigorakis.cultivo.plants.repository.PlantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class PlantServiceImpl implements PlantService {
    private final PlantRepository plantRepository;
    private final PlantSpeciesRepository plantSpeciesRepository;
    private final PlantVarietyRepository plantVarietyRepository;
    private final PlantMapper plantMapper;

    @Transactional(readOnly = true)
    @Override
    public Page<PlantResponse> getAllPlants(PageFilterRequest filterRequest, PageSortRequest sortRequest, boolean archived) {
        Pageable pageable = PageRequest.of(filterRequest.page(), filterRequest.size(), sortRequest.createSort("id"));
        Page<Plant> plants = archived ? plantRepository.findAllByArchivedAtIsNotNull(pageable) :
                plantRepository.findAllByArchivedAtIsNull(pageable);

        return plants.map(plantMapper::toResponse);
    }

    @Transactional(readOnly = true)
    @Override
    public PlantResponse getPlantById(Long id) {
        Plant plant = plantRepository.findByIdWithSpeciesAndVariety(id).orElseThrow(() -> {
            log.warn("No plant found with id {}", id);
            return new ResourceNotFoundException("Plant with id " + id + " not found");
        });

        return plantMapper.toResponse(plant);
    }

    @Transactional
    @Override
    public PlantResponse createPlant(PlantRequest request) {
        PlantSpecies species = findSpeciesById(request.plantSpeciesId());
        PlantVariety variety = findVarietyById(request.plantVarietyId());

        validateVarietyBelongsToSpecies(variety, species);

        Plant plant = plantMapper.toEntity(request, species, variety);
        Plant savedPlant = plantRepository.save(plant);
        log.info("Created plant with label {}", savedPlant.getLabel());

        return plantMapper.toResponse(savedPlant);
    }

    @Transactional
    @Override
    public PlantResponse updatePlantById(Long id, PlantRequest request) {
        Plant plant = plantRepository.findById(id).orElseThrow(() -> {
            log.warn("No plant found with id {}", id);
            return new ResourceNotFoundException("Plant with id " + id + " not found");
        });

        validatePlantIsNotArchived(plant);
        PlantSpecies species = findSpeciesById(request.plantSpeciesId());
        PlantVariety variety = findVarietyById(request.plantVarietyId());
        validateVarietyBelongsToSpecies(variety, species);

        Plant savedPlant = plantMapper.toUpdate(plant, request, species, variety);
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

        validatePlantIsNotArchived(plant);

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

    @Override
    public void archivePlantById(Long id) {
        Plant plant = plantRepository.findById(id).orElseThrow(() -> {
            log.warn("No plant found with id {}", id);
            return new ResourceNotFoundException("Plant with id " + id + " not found");
        });

        if(plant.getArchivedAt() != null) {
            log.warn("Plant with id {} is already archived", id);
            throw new ConflictException("Plant with id " + id + " is already archived", "ALREADY_ARCHIVED");
        }

        plant.setArchivedAt(LocalDateTime.now());
        plantRepository.save(plant);
        log.info("Archived plant with id {}", id);
    }

    private void validatePlantIsNotArchived(Plant plant) {
        if(plant.getArchivedAt() != null) {
            log.warn("Plant with id {} is archived and cannot modified", plant.getId());
            throw new ConflictException("Plant with id " + plant.getId() +
                                                " is archived and cannot modified", "PLANT_ARCHIVED");
        }
    }

    private PlantSpecies findSpeciesById(Long speciesId) {
        return plantSpeciesRepository.findById(speciesId).orElseThrow(() -> {
            log.warn("Plant species with id {} not found", speciesId);
            return new ResourceNotFoundException("Plant species with id " + speciesId + " not found");
        });
    }

    private PlantVariety findVarietyById(Long varietyId) {
        return plantVarietyRepository.findById(varietyId).orElseThrow(() -> {
            log.warn("Plant variety with id {} not found", varietyId);
            return new ResourceNotFoundException("Plant variety with id " + varietyId + " not found");
        });
    }

    private void validateVarietyBelongsToSpecies(PlantVariety variety, PlantSpecies species) {
        if(variety != null && !variety.belongsTo(species)) {
            log.warn("Plant variety with id {} does not belong to species with id {}", variety.getId(), species.getId());
            throw new ConflictException("Plant variety with id " + variety.getId() +
                                                " does not belong to species with id " + species.getId());
        }
    }
}
