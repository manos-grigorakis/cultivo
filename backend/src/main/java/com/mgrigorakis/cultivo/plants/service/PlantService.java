package com.mgrigorakis.cultivo.plants.service;

import com.mgrigorakis.cultivo.common.dto.PageFilterRequest;
import com.mgrigorakis.cultivo.common.dto.PageSortRequest;
import com.mgrigorakis.cultivo.plants.dto.PlantRequest;
import com.mgrigorakis.cultivo.plants.dto.PlantResponse;
import org.springframework.data.domain.Page;

public interface PlantService {
    Page<PlantResponse> getAllPlants(PageFilterRequest filterRequest, PageSortRequest sortRequest);

    PlantResponse getPlantById(Long id);

    PlantResponse createPlant(PlantRequest request);

    PlantResponse updatePlantById(Long id, PlantRequest request);
}
