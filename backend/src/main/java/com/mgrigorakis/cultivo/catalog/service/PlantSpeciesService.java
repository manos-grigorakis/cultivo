package com.mgrigorakis.cultivo.catalog.service;

import com.mgrigorakis.cultivo.common.dto.PageFilterRequest;
import com.mgrigorakis.cultivo.common.dto.PageSortRequest;
import com.mgrigorakis.cultivo.catalog.dto.PlantSpeciesRequest;
import com.mgrigorakis.cultivo.catalog.dto.PlantSpeciesResponse;
import org.springframework.data.domain.Page;

public interface PlantSpeciesService {
    Page<PlantSpeciesResponse> getAllPlantSpecies(PageFilterRequest filterRequest, PageSortRequest sortRequest);

    PlantSpeciesResponse getPlantSpeciesById(Long id);

    PlantSpeciesResponse createPlantSpecies(PlantSpeciesRequest request);

    PlantSpeciesResponse updatePlantSpeciesById(Long id, PlantSpeciesRequest request);

    void deletePlantSpeciesById(Long id);
}
