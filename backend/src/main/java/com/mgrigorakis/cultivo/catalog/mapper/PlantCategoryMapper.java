package com.mgrigorakis.cultivo.catalog.mapper;

import com.mgrigorakis.cultivo.catalog.dto.PlantCategoryRequest;
import com.mgrigorakis.cultivo.catalog.dto.PlantCategoryResponse;
import com.mgrigorakis.cultivo.catalog.model.PlantCategory;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PlantCategoryMapper {
    PlantCategory toEntity(PlantCategoryRequest request);

    PlantCategory toUpdate(@MappingTarget PlantCategory entity, PlantCategoryRequest request);

    PlantCategoryResponse toResponse(PlantCategory entity);
}
