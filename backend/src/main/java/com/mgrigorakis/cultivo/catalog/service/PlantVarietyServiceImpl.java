package com.mgrigorakis.cultivo.catalog.service;

import com.mgrigorakis.cultivo.catalog.dto.PlantVarietyRequest;
import com.mgrigorakis.cultivo.catalog.dto.PlantVarietyResponse;
import com.mgrigorakis.cultivo.catalog.mapper.PlantVarietyMapper;
import com.mgrigorakis.cultivo.catalog.model.PlantSpecies;
import com.mgrigorakis.cultivo.catalog.model.PlantVariety;
import com.mgrigorakis.cultivo.catalog.repository.PlantSpeciesRepository;
import com.mgrigorakis.cultivo.catalog.repository.PlantVarietyRepository;
import com.mgrigorakis.cultivo.common.dto.PageFilterRequest;
import com.mgrigorakis.cultivo.common.dto.PageSortRequest;
import com.mgrigorakis.cultivo.common.exceptions.DuplicateEntryException;
import com.mgrigorakis.cultivo.common.exceptions.ResourceNotFoundException;
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
public class PlantVarietyServiceImpl implements PlantVarietyService {
    private final PlantVarietyRepository plantVarietyRepository;
    private final PlantSpeciesRepository plantSpeciesRepository;
    private final PlantVarietyMapper plantVarietyMapper;

    @Override
    public Page<PlantVarietyResponse> getAllPlantVarieties(PageFilterRequest filterRequest, PageSortRequest sortRequest,
                                                           String name) {
        Pageable pageable = PageRequest.of(filterRequest.page(), filterRequest.size(), sortRequest.createSort("name"));
        return plantVarietyRepository.findByNameContainingIgnoreCase(name, pageable).map(plantVarietyMapper::toResponse);
    }

    @Override
    public PlantVarietyResponse getPlantVarietyById(Long id) {
        return plantVarietyMapper.toResponse(findPlantVarietyById(id));
    }

    @Override
    public PlantVariety findPlantVarietyById(Long id) {
        return plantVarietyRepository.findById(id).orElseThrow(() -> {
            log.warn("Plant variety with id {} not found", id);
            return new ResourceNotFoundException("Plant variety with id " + id + " not found");
        });
    }

    @Override
    public PlantVarietyResponse createPlantVariety(PlantVarietyRequest request) {
        if(plantVarietyRepository.existsByNameIgnoreCaseAndPlantSpecies_Id(request.name(), request.plantSpeciesId())) {
            log.warn("Plant variety with name {} already exists", request.name());
            throw new DuplicateEntryException("Plant variety with name " + request.name() + " already exists");
        }

        PlantSpecies plantSpecies = findPlantSpeciesById(request.plantSpeciesId());

        PlantVariety plantVariety = plantVarietyMapper.toEntity(request);
        plantVariety.setPlantSpecies(plantSpecies);
        PlantVariety saved = plantVarietyRepository.save(plantVariety);
        log.info("Plant variety with name {} created", saved.getName());

        return plantVarietyMapper.toResponse(saved);
    }

    @Transactional
    @Override
    public PlantVarietyResponse updatePlantVarietyById(Long id, PlantVarietyRequest request) {
        PlantVariety plantVariety = findPlantVarietyById(id);

        if (plantVarietyRepository.existsByNameIgnoreCaseAndIdNotAndPlantSpecies_Id(request.name(), id, request.plantSpeciesId())) {
            log.warn("Plant variety with id {} and name {} already exists", id, request.name());
            throw new DuplicateEntryException(
                    "Plant variety with id " + id + " name " + request.name() + " already exists");
        }

        PlantSpecies plantSpecies = findPlantSpeciesById(request.plantSpeciesId());

        PlantVariety update = plantVarietyMapper.toUpdate(plantVariety, request);
        update.setPlantSpecies(plantSpecies);
        PlantVariety saved = plantVarietyRepository.save(update);
        log.info("Plant variety with id {} updated", saved.getId());

        return plantVarietyMapper.toResponse(saved);
    }

    @Override
    public void deletePlantVarietyById(Long id) {
        PlantVariety plantVariety = findPlantVarietyById(id);
        plantVarietyRepository.delete(plantVariety);
        log.info("Plant variety with id {} deleted", id);
    }

    private PlantSpecies findPlantSpeciesById(Long id) {
        return plantSpeciesRepository.findById(id).orElseThrow(() -> {
            log.warn("Plant species with id {} not found", id);
            return new ResourceNotFoundException("Plant species with id " + id + " not found");
        });
    }
}
