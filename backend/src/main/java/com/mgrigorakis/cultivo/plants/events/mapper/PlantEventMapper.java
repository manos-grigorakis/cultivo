package com.mgrigorakis.cultivo.plants.events.mapper;

import com.mgrigorakis.cultivo.plants.events.dto.PlantEventRequest;
import com.mgrigorakis.cultivo.plants.events.dto.PlantEventResponse;
import com.mgrigorakis.cultivo.plants.events.model.PlantEvent;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PlantEventMapper {
    PlantEvent toEntity(PlantEventRequest request);

    PlantEventResponse toResponse(PlantEvent entity);
}
