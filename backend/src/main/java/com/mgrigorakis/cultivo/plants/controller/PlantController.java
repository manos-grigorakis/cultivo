package com.mgrigorakis.cultivo.plants.controller;

import com.mgrigorakis.cultivo.common.dto.ApiResponseWrapper;
import com.mgrigorakis.cultivo.plants.dto.PlantRequest;
import com.mgrigorakis.cultivo.plants.dto.PlantResponse;
import com.mgrigorakis.cultivo.plants.service.PlantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/v1/plants")
@RestController
public class PlantController {
    private final PlantService plantService;

    @GetMapping
    public ApiResponseWrapper<List<PlantResponse>>  getAllPlants() {
        return new ApiResponseWrapper<>(plantService.getAllPlants());
    }

    @GetMapping("/{id}")
    public ApiResponseWrapper<PlantResponse> getPlantById(@PathVariable Long id) {
        return new ApiResponseWrapper<>(plantService.getPlantById(id));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ApiResponseWrapper<PlantResponse> createPlant(@RequestBody @Valid PlantRequest request) {
        return new ApiResponseWrapper<>(plantService.createPlant(request));
    }
}
