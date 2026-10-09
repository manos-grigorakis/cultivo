package com.mgrigorakis.cultivo.plants.mapper;

import com.mgrigorakis.cultivo.plants.dto.PlantRequest;
import com.mgrigorakis.cultivo.plants.dto.PlantResponse;
import com.mgrigorakis.cultivo.plants.model.Plant;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PlantMapper {
    Plant toEntity(PlantRequest request);

    PlantResponse toResponse(Plant entity);
}
