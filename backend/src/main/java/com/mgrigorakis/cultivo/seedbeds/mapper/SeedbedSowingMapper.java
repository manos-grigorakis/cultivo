package com.mgrigorakis.cultivo.seedbeds.mapper;

import com.mgrigorakis.cultivo.seedbeds.dto.SeedbedSowingRequest;
import com.mgrigorakis.cultivo.seedbeds.dto.SeedbedSowingResponse;
import com.mgrigorakis.cultivo.seedbeds.model.SeedbedSowing;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SeedbedSowingMapper {
    @Mapping(target = "slot", ignore = true)
    @Mapping(target = "packet", ignore = true)
    SeedbedSowing toEntity(SeedbedSowingRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "completedAt", ignore = true)
    @Mapping(target = "slot", ignore = true)
    @Mapping(target = "packet", ignore = true)
    SeedbedSowing toUpdate(@MappingTarget SeedbedSowing entity, SeedbedSowingRequest request);

    @Mapping(target = "id", source = "entity.id")
    @Mapping(target = "seedbedSlot", source = "slot")
    @Mapping(target = "seedPacket", source = "packet")
    SeedbedSowingResponse toResponse(SeedbedSowing entity);
}
