package com.mgrigorakis.cultivo.seeds.mapper;

import com.mgrigorakis.cultivo.catalog.model.PlantVariety;
import com.mgrigorakis.cultivo.seeds.dto.PlantVarietySummary;
import com.mgrigorakis.cultivo.seeds.dto.SeedPacketCreateRequest;
import com.mgrigorakis.cultivo.seeds.dto.SeedPacketResponse;
import com.mgrigorakis.cultivo.seeds.dto.SeedPacketUpdateRequest;
import com.mgrigorakis.cultivo.seeds.model.SeedPacket;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SeedPacketMapper {
    @Mapping(target = "plantVariety", ignore = true)
    SeedPacket toEntity(SeedPacketCreateRequest request);

    @Mapping(target = "plantVariety", ignore = true)
    SeedPacket toUpdate(@MappingTarget SeedPacket seedPacket, SeedPacketUpdateRequest request);

    @Mapping(target = "id", source = "seedPacket.id")
    SeedPacketResponse toResponse(SeedPacket seedPacket);

    PlantVarietySummary toPlantVarietySummary(PlantVariety plantVariety);
}
