package com.mgrigorakis.cultivo.plants.events.controller;

import com.mgrigorakis.cultivo.common.dto.ApiResponseWrapper;
import com.mgrigorakis.cultivo.common.dto.PageFilterRequest;
import com.mgrigorakis.cultivo.common.dto.PageSortRequest;
import com.mgrigorakis.cultivo.plants.events.dto.PlantEventRequest;
import com.mgrigorakis.cultivo.plants.events.dto.PlantEventResponse;
import com.mgrigorakis.cultivo.plants.events.service.PlantEventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1/plants/{plantId}/events")
@RestController
public class PlantEventController {
    private final PlantEventService plantEventService;

    @Operation(summary = "Get All Plant Events by Plant ID",
            description = "List of plant events of a specific plant with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of plant events with pagination for a plant"),
            @ApiResponse(responseCode = "404", description = "Plant doesn't exist"),
    })
    @GetMapping
    public ApiResponseWrapper<Page<PlantEventResponse>> getAllPlantEventsByPlantId(
            @PathVariable Long plantId,
            @ModelAttribute @Valid PageFilterRequest filterRequest,
            @RequestParam(defaultValue = "occurredAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        PageSortRequest sortRequest = new PageSortRequest(sortBy, sortDirection);

        return new ApiResponseWrapper<>(
                plantEventService.getAllPlantEventsByPlantId(plantId, filterRequest, sortRequest));
    }

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
