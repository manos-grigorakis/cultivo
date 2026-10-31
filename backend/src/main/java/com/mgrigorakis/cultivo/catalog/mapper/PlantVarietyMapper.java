package com.mgrigorakis.cultivo.catalog.mapper;

import com.mgrigorakis.cultivo.catalog.dto.PlantVarietyRequest;
import com.mgrigorakis.cultivo.catalog.dto.PlantVarietyResponse;
import com.mgrigorakis.cultivo.catalog.model.PlantVariety;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PlantVarietyMapper {
    PlantVariety toEntity(PlantVarietyRequest request);

    PlantVariety toUpdate(@MappingTarget PlantVariety entity, PlantVarietyRequest request);

    @Mapping(target = "plantSpeciesId", source = "entity.plantSpecies.id")
    PlantVarietyResponse toResponse(PlantVariety entity);
}
