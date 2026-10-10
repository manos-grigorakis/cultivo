package com.mgrigorakis.cultivo.plants.service;

import com.mgrigorakis.cultivo.common.dto.PageFilterRequest;
import com.mgrigorakis.cultivo.common.dto.PageSortRequest;
import com.mgrigorakis.cultivo.plants.dto.PlantRequest;
import com.mgrigorakis.cultivo.plants.dto.PlantResponse;
import com.mgrigorakis.cultivo.plants.dto.PlantStatusRequest;
import com.mgrigorakis.cultivo.plants.model.enums.PlantStatus;
import org.springframework.data.domain.Page;

public interface PlantService {
    Page<PlantResponse> getAllPlants(PageFilterRequest filterRequest, PageSortRequest sortRequest, boolean archived);

    PlantResponse getPlantById(Long id);

    PlantResponse createPlant(PlantRequest request);

    PlantResponse updatePlantById(Long id, PlantRequest request);

    void updatePlantStatusById(Long id, PlantStatusRequest request);

    void archivePlantById(Long id);
}
