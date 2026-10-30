package com.mgrigorakis.cultivo.catalog.service;

import com.mgrigorakis.cultivo.common.dto.PageFilterRequest;
import com.mgrigorakis.cultivo.common.dto.PageSortRequest;
import com.mgrigorakis.cultivo.common.exceptions.DuplicateEntryException;
import com.mgrigorakis.cultivo.common.exceptions.ResourceNotFoundException;
import com.mgrigorakis.cultivo.catalog.dto.PlantSpeciesRequest;
import com.mgrigorakis.cultivo.catalog.dto.PlantSpeciesResponse;
import com.mgrigorakis.cultivo.catalog.mapper.PlantSpeciesMapper;
import com.mgrigorakis.cultivo.catalog.model.PlantCategory;
import com.mgrigorakis.cultivo.catalog.model.PlantSpecies;
import com.mgrigorakis.cultivo.catalog.repository.PlantCategoryRepository;
import com.mgrigorakis.cultivo.catalog.repository.PlantSpeciesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Service
public class PlantSpeciesServiceImpl implements PlantSpeciesService {
    private final PlantSpeciesRepository plantSpeciesRepository;
    private final PlantCategoryRepository plantCategoryRepository;
    private final PlantSpeciesMapper plantSpeciesMapper;

    @Transactional(readOnly = true)
    @Override
    public Page<PlantSpeciesResponse> getAllPlantSpecies(PageFilterRequest filterRequest, PageSortRequest sortRequest) {
        Pageable pageable = PageRequest.of(filterRequest.page(), filterRequest.size(), sortRequest.createSort("name"));
        return plantSpeciesRepository.findAll(pageable).map(plantSpeciesMapper::toResponse);
    }


    @Transactional(readOnly = true)
    @Override
    public PlantSpeciesResponse getPlantSpeciesById(Long id) {
        PlantSpecies plantSpecies = findPlantSpeciesById(id);

        return plantSpeciesMapper.toResponse(plantSpecies);
    }

    @Transactional
    @Override
    public PlantSpeciesResponse createPlantSpecies(PlantSpeciesRequest request) {
        validateUniqueness(request, null);

        PlantCategory plantCategory = findPlantCategoryById(request.plantCategoryId());

        PlantSpecies plantSpecies = plantSpeciesMapper.toEntity(request);
        plantSpecies.setPlantCategory(plantCategory);
        PlantSpecies saved = plantSpeciesRepository.save(plantSpecies);
        log.info("Plant species with id {} has been created", saved.getId());

        return plantSpeciesMapper.toResponse(saved);
    }

    @Transactional
    @Override
    public PlantSpeciesResponse updatePlantSpeciesById(Long id, PlantSpeciesRequest request) {
        PlantSpecies foundedPlantSpecies = findPlantSpeciesById(id);

        validateUniqueness(request, id);

        PlantCategory plantCategory = findPlantCategoryById(request.plantCategoryId());

        PlantSpecies plantSpecies = plantSpeciesMapper.toUpdate(foundedPlantSpecies, request);
        plantSpecies.setPlantCategory(plantCategory);
        PlantSpecies saved = plantSpeciesRepository.save(plantSpecies);

        log.info("Plant species with id {} updated", saved.getId());

        return plantSpeciesMapper.toResponse(saved);
    }

    @Transactional
    @Override
    public void deletePlantSpeciesById(Long id) {
        PlantSpecies plantSpecies = plantSpeciesRepository.findById(id).orElseThrow(() -> {
            log.warn("Plant species with id {} not found", id);
            return new ResourceNotFoundException("Plant species with id " + id + " not found");
        });;

        plantSpeciesRepository.delete(plantSpecies);
        log.info("Plant species with id {} has been deleted", id);
    }

    /**
     * Validates the name and scientific name are not used by another plant species
     *
     * @param request   The requests to validate
     * @param excludeId The plant species ID for update operations, or {@code null} on create
     * @throws DuplicateEntryException if either value is already in use
     */
    private void validateUniqueness(PlantSpeciesRequest request, Long excludeId) {
        validateNameUnique(request.name(), excludeId);
        validateScientificNameUnique(request.scientificName(), excludeId);
    }

    /**
     * Validates that the name is not used by another plant species
     *
     * @param name      The name to validate for uniqueness
     * @param excludeId The plant species ID for update operations, or {@code null} on create
     * @throws DuplicateEntryException If the name is already in use
     */
    private void validateNameUnique(String name, Long excludeId) {
        boolean exists = excludeId == null
                ? plantSpeciesRepository.existsByNameIgnoreCase(name)
                : plantSpeciesRepository.existsByNameIgnoreCaseAndIdNot(name, excludeId);

        if (exists) {
            log.warn("Plant species with name {} already exists", name);
            throw new DuplicateEntryException("Plant species with name " + name + " already exists");
        }
    }

    /**
     * Validates that the scientific name is not used by another plant species
     *
     * @param scientificName The scientific name to validate for uniqueness
     * @param excludeId      The plant species ID for update operations, or {@code null} on create
     * @throws DuplicateEntryException If the scientific name is already in use
     */
    private void validateScientificNameUnique(String scientificName, Long excludeId) {
        if (scientificName == null) return;

        boolean exists = excludeId == null
                ? plantSpeciesRepository.existsByScientificNameIgnoreCase(scientificName)
                : plantSpeciesRepository.existsByScientificNameIgnoreCaseAndIdNot(scientificName, excludeId);

        if (exists) {
            log.warn("Plant species with scientific name {} already exists", scientificName);
            throw new DuplicateEntryException(
                    "Plant species with scientific name " + scientificName + " already exists");
        }
    }

    /**
     * Finds a plant species using the provided id
     *
     * @param id The plant species id
     * @return The founded plant species if exist
     * @throws ResourceNotFoundException If the plant species does not exist by the provided id
     */
    private PlantSpecies findPlantSpeciesById(Long id) {
        return plantSpeciesRepository.findWithPlantCategoryById(id).orElseThrow(() -> {
            log.warn("Plant species with id {} not found", id);
            return new ResourceNotFoundException("Plant species with id " + id + " not found");
        });
    }

    /**
     * Finds a plant category using the provided id
     *
     * @param id The {@link PlantCategory} ID
     * @return The founded plant category if exist
     * @throws ResourceNotFoundException If the plant category does not exist by the provided id
     */
    private PlantCategory findPlantCategoryById(Long id) {
        return plantCategoryRepository.findById(id).orElseThrow(() -> {
            log.warn("Plant category with id {} not found", id);
            return new ResourceNotFoundException("Plant category with id " + id + " not found");
        });
    }
}
