package com.mgrigorakis.cultivo.seedbeds.mapper;

import com.mgrigorakis.cultivo.seedbeds.dto.SeedbedSlotSummaryResponse;
import com.mgrigorakis.cultivo.seedbeds.model.SeedbedSlot;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SeedbedSlotMapper {
    SeedbedSlotSummaryResponse toSummaryResponse(SeedbedSlot slot);
}
