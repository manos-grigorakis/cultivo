package com.mgrigorakis.cultivo.plants.events.controller;

import com.mgrigorakis.cultivo.common.dto.ApiResponseWrapper;
import com.mgrigorakis.cultivo.plants.events.dto.PlantEventRequest;
import com.mgrigorakis.cultivo.plants.events.dto.PlantEventResponse;
import com.mgrigorakis.cultivo.plants.events.service.PlantEventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1/plants/{plantId}/events")
@RestController
public class PlantEventController {
    private final PlantEventService plantEventService;

    @Operation(summary = "Create a Plant Event", description = "Creates a plant event for the selected plant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Plant event created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "404", description = "Plant doesn't exist"),
            @ApiResponse(responseCode = "409", description = "Plant is archived")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping()
    public ApiResponseWrapper<PlantEventResponse> createPlantEvent(@PathVariable Long plantId,
                                                                   @RequestBody @Valid PlantEventRequest request) {
        return new ApiResponseWrapper<>(plantEventService.createPlantEvent(plantId, request));
    }
}
