package com.mgrigorakis.cultivo.catalog.service;

import com.mgrigorakis.cultivo.catalog.dto.PlantVarietyRequest;
import com.mgrigorakis.cultivo.catalog.dto.PlantVarietyResponse;
import com.mgrigorakis.cultivo.catalog.model.PlantVariety;
import com.mgrigorakis.cultivo.common.dto.PageFilterRequest;
import com.mgrigorakis.cultivo.common.dto.PageSortRequest;
import org.springframework.data.domain.Page;

public interface PlantVarietyService {
    Page<PlantVarietyResponse> getAllPlantVarieties(PageFilterRequest filterRequest, PageSortRequest sortRequest,
                                                    String name);

    PlantVarietyResponse getPlantVarietyById(Long id);

    PlantVariety findPlantVarietyById(Long id);

    PlantVarietyResponse createPlantVariety(PlantVarietyRequest request);

    PlantVarietyResponse updatePlantVarietyById(Long id, PlantVarietyRequest request);

    void deletePlantVarietyById(Long id);
}
