package com.mgrigorakis.cultivo.plants.service;

import com.mgrigorakis.cultivo.plants.dto.PlantRequest;
import com.mgrigorakis.cultivo.plants.dto.PlantResponse;

import java.util.List;

public interface PlantService {
    List<PlantResponse> getAllPlants();

    PlantResponse getPlantById(Long id);

    PlantResponse createPlant(PlantRequest request);
}
