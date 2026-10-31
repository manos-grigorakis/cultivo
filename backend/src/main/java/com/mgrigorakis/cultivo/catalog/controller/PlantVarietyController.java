package com.mgrigorakis.cultivo.catalog.controller;

import com.mgrigorakis.cultivo.catalog.dto.PlantVarietyRequest;
import com.mgrigorakis.cultivo.catalog.dto.PlantVarietyResponse;
import com.mgrigorakis.cultivo.catalog.service.PlantVarietyService;
import com.mgrigorakis.cultivo.common.dto.ApiResponseWrapper;
import com.mgrigorakis.cultivo.common.dto.PageFilterRequest;
import com.mgrigorakis.cultivo.common.dto.PageSortRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Plant Varieties")
@RequiredArgsConstructor
@RequestMapping("/api/v1/plant-varieties")
@RestController
public class PlantVarietyController {
    private final PlantVarietyService plantVarietyService;

    @Operation(summary = "Get All Plant Varieties", description = "Lists all plant varieties with pagination")
    @ApiResponse(responseCode = "200", description =
            "List of all plant varieties with pagination, sorting and optional name and species filtering")
    @GetMapping
    public ApiResponseWrapper<Page<PlantVarietyResponse>> getAllPlantVarieties(
            @ModelAttribute @Valid PageFilterRequest filterRequest,
            @ModelAttribute PageSortRequest sortRequest,
            @RequestParam(defaultValue = "") String name,
            @RequestParam(required = false) Long plantSpeciesId
            ) {
        return new ApiResponseWrapper<>(
                plantVarietyService.getAllPlantVarieties(filterRequest, sortRequest, name, plantSpeciesId));
    }

    @Operation(summary = "Get Plant Variety by ID", description = "Finds a plant variety by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Founded plant variety"),
            @ApiResponse(responseCode = "404", description = "Plant variety doesn't exist")
    })
    @GetMapping("/{id}")
    public ApiResponseWrapper<PlantVarietyResponse> getPlantVarietyById(@PathVariable Long id) {
        return new ApiResponseWrapper<>(plantVarietyService.getPlantVarietyById(id));
    }

    @Operation(summary = "Create a Plant Variety", description = "Create a new plant variety")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Plant variety created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "404", description = "Plant species doesn't exist"),
            @ApiResponse(responseCode = "409", description = "Plant variety already exists")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ApiResponseWrapper<PlantVarietyResponse> createPlantVariety(@RequestBody @Valid PlantVarietyRequest request) {
        return new ApiResponseWrapper<>(plantVarietyService.createPlantVariety(request));
    }

    @Operation(summary = "Update a Plant Variety by ID", description = "Update an existing plant variety by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Plant variety updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "404", description = "Plant variety or plant species doesn't exist"),
            @ApiResponse(responseCode = "409", description = "Plant variety already exists")
    })
    @PutMapping("/{id}")
    public ApiResponseWrapper<PlantVarietyResponse> updatePlantVarietyById(
            @PathVariable Long id, @RequestBody @Valid PlantVarietyRequest request) {
        return new ApiResponseWrapper<>(plantVarietyService.updatePlantVarietyById(id, request));
    }

    @Operation(summary = "Delete a Plant Variety by ID", description = "Delete a plant variety by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Plant variety deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Plant variety doesn't exist"),
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deletePlantVarietyById(@PathVariable Long id) {
        plantVarietyService.deletePlantVarietyById(id);
    }
}
