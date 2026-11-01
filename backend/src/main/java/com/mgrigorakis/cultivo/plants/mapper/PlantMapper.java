package com.mgrigorakis.cultivo.plants.mapper;

import com.mgrigorakis.cultivo.catalog.model.PlantSpecies;
import com.mgrigorakis.cultivo.catalog.model.PlantVariety;
import com.mgrigorakis.cultivo.plants.dto.PlantReferenceResponse;
import com.mgrigorakis.cultivo.plants.dto.PlantRequest;
import com.mgrigorakis.cultivo.plants.dto.PlantResponse;
import com.mgrigorakis.cultivo.plants.model.Plant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PlantMapper {
    Plant toEntity(PlantRequest request, PlantSpecies species, PlantVariety variety);

    @Mapping(ignore = true, target = "id")
    @Mapping(ignore = true, target = "createdAt")
    @Mapping(ignore = true, target = "updatedAt")
    Plant toUpdate(@MappingTarget Plant entity, PlantRequest request, PlantSpecies species, PlantVariety variety);


    @Mapping(source = "entity.id", target = "id")
    @Mapping(source = "entity.createdAt", target = "createdAt")
    @Mapping(source = "entity.updatedAt", target = "updatedAt")
    PlantResponse toResponse(Plant entity);

    PlantReferenceResponse toReference(PlantSpecies species);

    PlantReferenceResponse toReference(PlantVariety variety);
}
