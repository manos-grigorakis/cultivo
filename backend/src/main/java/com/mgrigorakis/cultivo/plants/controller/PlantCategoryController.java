package com.mgrigorakis.cultivo.plants.controller;

import com.mgrigorakis.cultivo.common.dto.ApiResponseWrapper;
import com.mgrigorakis.cultivo.common.dto.PageFilterRequest;
import com.mgrigorakis.cultivo.common.dto.PageSortRequest;
import com.mgrigorakis.cultivo.plants.dto.PlantCategoryRequest;
import com.mgrigorakis.cultivo.plants.dto.PlantCategoryResponse;
import com.mgrigorakis.cultivo.plants.service.PlantCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Plant Categories")
@RequiredArgsConstructor
@RequestMapping("/api/v1/plant-categories")
@RestController
public class PlantCategoryController {
    private final PlantCategoryService plantCategoryService;

    @Operation(summary = "Get All Plant Categories", description = "Lists all plant categories with pagination")
    @ApiResponse(responseCode = "200", description = "List of all plant categories with pagination")
    @GetMapping
    public ApiResponseWrapper<Page<PlantCategoryResponse>> getAllPlantCategories(
            @ModelAttribute @Valid PageFilterRequest filterRequest,
            @ModelAttribute PageSortRequest sortRequest) {
        return new ApiResponseWrapper<>(plantCategoryService.getAllPlantCategories(filterRequest, sortRequest));
    }

    @Operation(summary = "Get Plant Category by ID", description = "Finds a plant category by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Founded plant category"),
            @ApiResponse(responseCode = "404", description = "Plant category doesn't exist")
    })
    @GetMapping("/{id}")
    public ApiResponseWrapper<PlantCategoryResponse> getPlantCategoryById(@PathVariable Long id) {
        return new ApiResponseWrapper<>(plantCategoryService.getPlantCategoryById(id));
    }

    @Operation(summary = "Create a Plant Category", description = "Create a new plant category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Plant category created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "409", description = "Plant category already exists")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ApiResponseWrapper<PlantCategoryResponse> createPlantCategory(@RequestBody @Valid PlantCategoryRequest request) {
        return new ApiResponseWrapper<>(plantCategoryService.createPlantCategory(request));
    }

    @Operation(summary = "Update a Plant Category by ID", description = "Update an existing plant category by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Plant category updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "404", description = "Plant category doesn't exist"),
            @ApiResponse(responseCode = "409", description = "Plant category already exists")
    })
    @PutMapping("/{id}")
    public ApiResponseWrapper<PlantCategoryResponse> updatePlantCategoryById(@PathVariable Long id,
            @RequestBody @Valid PlantCategoryRequest request) {
        return new ApiResponseWrapper<>(plantCategoryService.updatePlantCategoryById(id, request));
    }

    @Operation(summary = "Delete a Plant Category by ID", description = "Delete a plant category by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Plant category deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Plant category doesn't exist"),
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deletePlantCategoryById(@PathVariable Long id) {
        plantCategoryService.deletePlantCategoryById(id);
    }
}
