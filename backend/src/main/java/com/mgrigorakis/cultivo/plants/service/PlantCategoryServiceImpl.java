package com.mgrigorakis.cultivo.plants.service;

import com.mgrigorakis.cultivo.common.dto.PageFilterRequest;
import com.mgrigorakis.cultivo.common.dto.PageSortRequest;
import com.mgrigorakis.cultivo.common.exceptions.DuplicateEntryException;
import com.mgrigorakis.cultivo.common.exceptions.ResourceNotFoundException;
import com.mgrigorakis.cultivo.plants.dto.PlantCategoryRequest;
import com.mgrigorakis.cultivo.plants.dto.PlantCategoryResponse;
import com.mgrigorakis.cultivo.plants.mapper.PlantCategoryMapper;
import com.mgrigorakis.cultivo.plants.model.PlantCategory;
import com.mgrigorakis.cultivo.plants.repository.PlantCategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class PlantCategoryServiceImpl implements PlantCategoryService {
    private final PlantCategoryRepository plantCategoryRepository;
    private final PlantCategoryMapper plantCategoryMapper;

    @Override
    public List<PlantCategoryResponse> getAllPlantCategories(PageFilterRequest filterRequest, PageSortRequest sortRequest) {
        Pageable pageable = PageRequest.of(filterRequest.page(), filterRequest.size(), sortRequest.createSort("name"));
        return plantCategoryRepository.findAll(pageable).stream().map(plantCategoryMapper::toResponse).toList();

    }

    @Override
    public PlantCategoryResponse getPlantCategoryById(Long id) {
        PlantCategory plantCategory = plantCategoryRepository.findById(id).orElseThrow(() -> {
            log.warn("Plant category with id {} not found", id);
            return new ResourceNotFoundException("Plant category with id " + id + " not found");
        });

        return plantCategoryMapper.toResponse(plantCategory);
    }

    @Override
    public PlantCategoryResponse createPlantCategory(PlantCategoryRequest request) {
        if(plantCategoryRepository.existsByNameIgnoreCase(request.name())) {
            log.warn("Attempt to create plant category with name {} already exists", request.name());
            throw new DuplicateEntryException("Plant category with name " + request.name() + " already exists");
        }

        PlantCategory plantCategory = plantCategoryMapper.toEntity(request);
        PlantCategory saved = plantCategoryRepository.save(plantCategory);
        log.info("Created plant category with name {}", saved.getName());

        return plantCategoryMapper.toResponse(saved);
    }

    @Override
    public PlantCategoryResponse updatePlantCategoryById(Long id, PlantCategoryRequest request) {
        PlantCategory plantCategory = plantCategoryRepository.findById(id).orElseThrow(() -> {
            log.warn("Plant category with id {} not found", id);
            return new ResourceNotFoundException("Plant category with id " + id + " not found");
        });

        if(plantCategoryRepository.existsByNameIgnoreCaseAndIdNot(request.name(), id)) {
            log.warn("Attempt to update plant category with id {} and name {} already exists", id, request.name());
            throw new DuplicateEntryException("Plant category with id " + id + " name " + request.name() + " already exists");
        }

        PlantCategory updated = plantCategoryRepository.save(plantCategoryMapper.toUpdate(plantCategory, request));
        log.info("Updated plant category with id {}", updated.getId());

        return plantCategoryMapper.toResponse(updated);
    }

    @Override
    public void deletePlantCategoryById(Long id) {
        PlantCategory plantCategory = plantCategoryRepository.findById(id).orElseThrow(() -> {
            log.warn("Plant category with id {} not found", id);
            return new ResourceNotFoundException("Plant category with id " + id + " not found");
        });

        plantCategoryRepository.delete(plantCategory);
        log.info("Deleted plant category with id {}", id);
    }
}
