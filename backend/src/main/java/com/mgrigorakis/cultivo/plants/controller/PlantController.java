package com.mgrigorakis.cultivo.plants.controller;

import com.mgrigorakis.cultivo.common.dto.ApiResponseWrapper;
import com.mgrigorakis.cultivo.common.dto.PageFilterRequest;
import com.mgrigorakis.cultivo.common.dto.PageSortRequest;
import com.mgrigorakis.cultivo.plants.dto.PlantRequest;
import com.mgrigorakis.cultivo.plants.dto.PlantResponse;
import com.mgrigorakis.cultivo.plants.service.PlantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Plants")
@RequiredArgsConstructor
@RequestMapping("/api/v1/plants")
@RestController
public class PlantController {
    private final PlantService plantService;

    @Operation(summary = "Get All Plants", description = "Lists all plants with pagination")
    @ApiResponse(responseCode = "200", description = "List of all plants with pagination")
    @GetMapping
    public ApiResponseWrapper<Page<PlantResponse>> getAllPlants(
            @ModelAttribute @Valid PageFilterRequest filterRequest,
            @ModelAttribute PageSortRequest sortRequest) {
        return new ApiResponseWrapper<>(plantService.getAllPlants(filterRequest, sortRequest));
    }

    @Operation(summary = "Get Plant by ID", description = "Finds a plant by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Founded plant"),
            @ApiResponse(responseCode = "404", description = "Plant doesn't exist")
    })
    @GetMapping("/{id}")
    public ApiResponseWrapper<PlantResponse> getPlantById(@PathVariable Long id) {
        return new ApiResponseWrapper<>(plantService.getPlantById(id));
    }

    @Operation(summary = "Create a Plant", description = "Create a new plant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Plant created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ApiResponseWrapper<PlantResponse> createPlant(@RequestBody @Valid PlantRequest request) {
        return new ApiResponseWrapper<>(plantService.createPlant(request));
    }
}
