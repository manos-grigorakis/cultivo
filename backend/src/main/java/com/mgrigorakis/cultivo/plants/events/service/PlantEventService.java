package com.mgrigorakis.cultivo.plants.events.service;

import com.mgrigorakis.cultivo.common.dto.PageFilterRequest;
import com.mgrigorakis.cultivo.common.dto.PageSortRequest;
import com.mgrigorakis.cultivo.plants.events.dto.PlantEventRequest;
import com.mgrigorakis.cultivo.plants.events.dto.PlantEventResponse;
import org.springframework.data.domain.Page;

public interface PlantEventService {
    Page<PlantEventResponse> getAllPlantEventsByPlantId(Long plantId, PageFilterRequest filterRequest,
                                                        PageSortRequest sortRequest);

    PlantEventResponse createPlantEvent(Long plantId, PlantEventRequest request);
}
