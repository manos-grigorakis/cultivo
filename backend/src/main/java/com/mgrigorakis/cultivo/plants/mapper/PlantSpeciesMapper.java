package com.mgrigorakis.cultivo.plants.mapper;

import com.mgrigorakis.cultivo.plants.dto.PlantSpeciesRequest;
import com.mgrigorakis.cultivo.plants.dto.PlantSpeciesResponse;
import com.mgrigorakis.cultivo.plants.model.PlantSpecies;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PlantSpeciesMapper {
    PlantSpecies toEntity(PlantSpeciesRequest request);

    PlantSpecies toUpdate(@MappingTarget PlantSpecies entity, PlantSpeciesRequest request);

    PlantSpeciesResponse toResponse(PlantSpecies entity);
}
