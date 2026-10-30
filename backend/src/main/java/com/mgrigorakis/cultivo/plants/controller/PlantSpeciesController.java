package com.mgrigorakis.cultivo.plants.controller;

import com.mgrigorakis.cultivo.common.dto.ApiResponseWrapper;
import com.mgrigorakis.cultivo.common.dto.PageFilterRequest;
import com.mgrigorakis.cultivo.common.dto.PageSortRequest;
import com.mgrigorakis.cultivo.plants.dto.PlantSpeciesRequest;
import com.mgrigorakis.cultivo.plants.dto.PlantSpeciesResponse;
import com.mgrigorakis.cultivo.plants.service.PlantSpeciesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1/plant-species")
@RestController
public class PlantSpeciesController {
    private final PlantSpeciesService plantSpeciesService;

    @Operation(summary = "Get All Plant Species", description = "Lists all plant species with pagination")
    @ApiResponse(responseCode = "200", description = "List of all plant species with pagination")
    @GetMapping
    public ApiResponseWrapper<Page<PlantSpeciesResponse>> getAllPlantSpecies(
            @ModelAttribute @Valid PageFilterRequest filterRequest,
            @ModelAttribute PageSortRequest sortRequest) {
        return new ApiResponseWrapper<>(plantSpeciesService.getAllPlantSpecies(filterRequest, sortRequest));
    }

    @Operation(summary = "Get Plant Species by ID", description = "Finds a plant species by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Founded plant species"),
            @ApiResponse(responseCode = "404", description = "Plant species or plant category doesn't exist")
    })
    @GetMapping("/{id}")
    public ApiResponseWrapper<PlantSpeciesResponse> getPlantSpeciesById(@PathVariable Long id) {
        return new ApiResponseWrapper<>(plantSpeciesService.getPlantSpeciesById(id));
    }

    @Operation(summary = "Create a Plant Species", description = "Create a new plant species")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Plant species created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "404", description = "Plant category not found"),
            @ApiResponse(responseCode = "409", description = "Name or scientific name already exists")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ApiResponseWrapper<PlantSpeciesResponse> createPlantSpecies(@RequestBody @Valid PlantSpeciesRequest request) {
        return new ApiResponseWrapper<>(plantSpeciesService.createPlantSpecies(request));
    }

    @Operation(summary = "Update a Plant Species by ID", description = "Update an existing plant species by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Plant species updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "404", description = "Plant species or plant category not found"),
            @ApiResponse(responseCode = "409", description = "Name or scientific name already exists")
    })
    @PutMapping("/{id}")
    public ApiResponseWrapper<PlantSpeciesResponse> updatePlantSpeciesById(@PathVariable Long id, @RequestBody @Valid PlantSpeciesRequest request) {
        return new ApiResponseWrapper<>(plantSpeciesService.updatePlantSpeciesById(id, request));
    }

    @Operation(summary = "Delete a Plant Species by ID", description = "Delete a plant species by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Plant species deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Plant species not found"),
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deletePlantSpeciesById(@PathVariable Long id) {
        plantSpeciesService.deletePlantSpeciesById(id);
    }
}
