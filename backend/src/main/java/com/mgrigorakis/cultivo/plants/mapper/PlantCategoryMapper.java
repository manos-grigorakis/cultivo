package com.mgrigorakis.cultivo.plants.mapper;

import com.mgrigorakis.cultivo.plants.dto.PlantCategoryRequest;
import com.mgrigorakis.cultivo.plants.dto.PlantCategoryResponse;
import com.mgrigorakis.cultivo.plants.model.PlantCategory;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PlantCategoryMapper {
    PlantCategory toEntity(PlantCategoryRequest request);

    PlantCategory toUpdate(@MappingTarget PlantCategory entity, PlantCategoryRequest request);

    PlantCategoryResponse toResponse(PlantCategory entity);
}
