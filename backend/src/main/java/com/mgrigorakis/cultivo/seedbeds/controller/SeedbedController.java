package com.mgrigorakis.cultivo.seedbeds.controller;

import com.mgrigorakis.cultivo.common.dto.ApiResponseWrapper;
import com.mgrigorakis.cultivo.common.dto.PageFilterRequest;
import com.mgrigorakis.cultivo.common.dto.PageSortRequest;
import com.mgrigorakis.cultivo.seedbeds.dto.*;
import com.mgrigorakis.cultivo.seedbeds.service.SeedbedService;
import com.mgrigorakis.cultivo.seedbeds.service.SeedbedSlotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/v1/seedbeds")
@RestController
public class SeedbedController {
    private final SeedbedService seedBedService;
    private final SeedbedSlotService seedbedSlotService;

    @Operation(summary = "Get All Seedbeds", description = "Lists all seedbeds with pagination")
    @ApiResponse(responseCode = "200", description = "List of all seedbeds with pagination")
    @GetMapping
    public ApiResponseWrapper<Page<SeedbedResponse>> getAllSeedbeds(
            @ModelAttribute @Valid PageFilterRequest filterRequest,
            @ModelAttribute PageSortRequest sortRequest) {
        return new ApiResponseWrapper<>(seedBedService.getAllSeedbeds(filterRequest, sortRequest));
    }

    @Operation(summary = "Get Seedbed by ID", description = "Finds a seedbed by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Founded seedbed"),
            @ApiResponse(responseCode = "404", description = "Seedbed doesn't exist")
    })
    @GetMapping("/{id}")
    public ApiResponseWrapper<SeedbedDetailResponse> getSeedbedById(@PathVariable Long id) {
        return new ApiResponseWrapper<>(seedBedService.getSeedbedById(id));
    }

    @Operation(summary = "Create a Seedbed", description =
            "Create a new seedbed along with its slots from the column and rows indexes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Seedbed created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "409", description = "Seedbed already exists by name"),
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ApiResponseWrapper<SeedbedDetailResponse> createSeedbed(@RequestBody @Valid SeedbedCreateRequest request) {
        return new ApiResponseWrapper<>(seedBedService.createSeedbed(request));
    }

    @Operation(summary = "Update a Seedbed by ID", description = "Update an existing seedbed by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Seedbed updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "404", description = "Seedbed not found"),
            @ApiResponse(responseCode = "409", description = "Seedbed already exists by name"),
    })
    @PutMapping("/{id}")
    public ApiResponseWrapper<SeedbedResponse> updateSeedbedById(@PathVariable Long id,
                                                                   @RequestBody @Valid SeedbedUpdateRequest request) {
        return new ApiResponseWrapper<>(seedBedService.updateSeedbedById(id, request));
    }

    @Operation(summary = "Delete a Seedbed by ID", description = "Delete an existing seedbed by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Seedbed deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Seedbed not found"),
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteSeedbedById(@PathVariable Long id) {
        seedBedService.deleteSeedbedById(id);
    }


    @Operation(summary = "Get All Seedbed Slots by Seedbed ID", description =
            "Finds all seedbed slots by the seedbed id")
    @ApiResponse(responseCode = "200", description = "Founded seedbed slot")
    @GetMapping("/{id}/slots")
    public ApiResponseWrapper<List<SeedbedSlotSummaryResponse>> getAllSeedbedSlotsSeedbedId(@PathVariable Long id) {
        return new ApiResponseWrapper<>(seedbedSlotService.getAllSeedbedSlotsBySeedbedId(id));
    }

    @Operation(summary = "Get Seedbed Slot by Seedbed ID and Slot Id", description =
            "Finds a seedbed slot by the seedbed id and the provided seedbed slot id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Founded seedbed slot"),
            @ApiResponse(responseCode = "404", description = "Seedbed slot not found"),
    })
    @GetMapping("/{id}/slots/{slotId}")
    public ApiResponseWrapper<SeedbedSlotSummaryResponse> getSeedbedSlotByIdAndSeedbedId(
            @PathVariable Long id, @PathVariable Long slotId) {
        return new ApiResponseWrapper<>(seedbedSlotService.getSeedbedSlotByIdAndSeedbedId(slotId, id));
    }
}
