package com.mgrigorakis.cultivo.plants.controller;

import com.mgrigorakis.cultivo.common.dto.ApiResponseWrapper;
import com.mgrigorakis.cultivo.common.dto.PageFilterRequest;
import com.mgrigorakis.cultivo.common.dto.PageSortRequest;
import com.mgrigorakis.cultivo.plants.dto.PlantCategoryRequest;
import com.mgrigorakis.cultivo.plants.dto.PlantCategoryResponse;
import com.mgrigorakis.cultivo.plants.service.PlantCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/v1/plant-categories")
@RestController
public class PlantCategoryController {
    private final PlantCategoryService plantCategoryService;

    @GetMapping
    public ApiResponseWrapper<List<PlantCategoryResponse>> getAllPlantCategories(
            @ModelAttribute @Valid PageFilterRequest filterRequest,
            @ModelAttribute PageSortRequest sortRequest) {
        return new ApiResponseWrapper<>(plantCategoryService.getAllPlantCategories(filterRequest, sortRequest));
    }

    @GetMapping("/{id}")
    public ApiResponseWrapper<PlantCategoryResponse> getPlantCategoryById(@PathVariable Long id) {
        return new ApiResponseWrapper<>(plantCategoryService.getPlantCategoryById(id));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ApiResponseWrapper<PlantCategoryResponse> createPlantCategory(@RequestBody @Valid PlantCategoryRequest request) {
        return new ApiResponseWrapper<>(plantCategoryService.createPlantCategory(request));
    }

    @PutMapping("/{id}")
    public ApiResponseWrapper<PlantCategoryResponse> updatePlantCategoryById(@PathVariable Long id,
            @RequestBody @Valid PlantCategoryRequest request) {
        return new ApiResponseWrapper<>(plantCategoryService.updatePlantCategoryById(id, request));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deletePlantCategoryById(@PathVariable Long id) {
        plantCategoryService.deletePlantCategoryById(id);
    }
}
