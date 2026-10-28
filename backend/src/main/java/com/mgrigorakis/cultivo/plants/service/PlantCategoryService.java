package com.mgrigorakis.cultivo.plants.service;

import com.mgrigorakis.cultivo.common.dto.PageFilterRequest;
import com.mgrigorakis.cultivo.common.dto.PageSortRequest;
import com.mgrigorakis.cultivo.plants.dto.PlantCategoryRequest;
import com.mgrigorakis.cultivo.plants.dto.PlantCategoryResponse;

import java.util.List;

public interface PlantCategoryService {
    List<PlantCategoryResponse> getAllPlantCategories(PageFilterRequest filterRequest, PageSortRequest sortRequest);

    PlantCategoryResponse getPlantCategoryById(Long id);

    PlantCategoryResponse createPlantCategory(PlantCategoryRequest request);

    PlantCategoryResponse updatePlantCategoryById(Long id, PlantCategoryRequest request);

    void deletePlantCategoryById(Long id);
}
