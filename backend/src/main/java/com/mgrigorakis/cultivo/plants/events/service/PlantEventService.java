package com.mgrigorakis.cultivo.plants.events.service;

import com.mgrigorakis.cultivo.plants.events.dto.PlantEventRequest;
import com.mgrigorakis.cultivo.plants.events.dto.PlantEventResponse;

public interface PlantEventService {
    PlantEventResponse createPlantEvent(Long plantId, PlantEventRequest request);
}
