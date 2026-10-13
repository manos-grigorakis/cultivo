package com.mgrigorakis.cultivo.plants.events.mapper;

import com.mgrigorakis.cultivo.plants.events.dto.PlantEventRequest;
import com.mgrigorakis.cultivo.plants.events.dto.PlantEventResponse;
import com.mgrigorakis.cultivo.plants.events.model.PlantEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PlantEventMapper {
    PlantEvent toEntity(PlantEventRequest request);

    PlantEvent toUpdate(@MappingTarget PlantEvent plantEvent, PlantEventRequest request);

    @Mapping(target = "plantId", source = "entity.plant.id")
    PlantEventResponse toResponse(PlantEvent entity);
}
