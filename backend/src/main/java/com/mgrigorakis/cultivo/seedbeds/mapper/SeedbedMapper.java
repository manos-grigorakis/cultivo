package com.mgrigorakis.cultivo.seedbeds.mapper;

import com.mgrigorakis.cultivo.seedbeds.dto.*;
import com.mgrigorakis.cultivo.seedbeds.model.Seedbed;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SeedbedMapper {
    Seedbed toEntity(SeedbedCreateRequest request);

    Seedbed toUpdate(@MappingTarget Seedbed seedbed, SeedbedUpdateRequest request);

    SeedbedResponse toResponse(Seedbed seedbed);

    @Mapping(target = "seedbedSlotsSummary", source = "seedbedSlotsSummary")
    SeedbedDetailResponse toResponse(Seedbed seedBed, List<SeedbedSlotSummaryResponse> seedbedSlotsSummary);
}
