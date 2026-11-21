package com.mgrigorakis.cultivo.seedbeds.controller;

import com.mgrigorakis.cultivo.common.dto.ApiResponseWrapper;
import com.mgrigorakis.cultivo.common.dto.PageFilterRequest;
import com.mgrigorakis.cultivo.common.dto.PageSortRequest;
import com.mgrigorakis.cultivo.seedbeds.dto.*;
import com.mgrigorakis.cultivo.seedbeds.model.enums.SeedbedSowingStatus;
import com.mgrigorakis.cultivo.seedbeds.service.SeedbedService;
import com.mgrigorakis.cultivo.seedbeds.service.SeedbedSlotService;
import com.mgrigorakis.cultivo.seedbeds.service.SeedbedSowingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Seedbed")
@RequiredArgsConstructor
@RequestMapping("/api/v1/seedbeds")
@RestController
public class SeedbedController {
    private final SeedbedService seedBedService;
    private final SeedbedSlotService seedbedSlotService;
    private final SeedbedSowingService seedbedSowingService;

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
            @ApiResponse(responseCode = "409", description = "Seedbed cannot be deleted because it contains sowings"),
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

    @Operation(summary = "Get All Seedbed Sowings by Seedbed ID", description =
            "Lists all seedbed sowings for a specific seedbed, with pagination and optional filtering by criteria")
    @ApiResponse(responseCode = "200", description = "Paginated list of all sowings belonging to the specified seedbed")
    @GetMapping("/{id}/sowings")
    public ApiResponseWrapper<Page<SeedbedSowingResponse>> getAllSeedbedSowingsBySeedbedId(
            @PathVariable Long id, @ModelAttribute @Valid PageFilterRequest filterRequest,
            @ModelAttribute PageSortRequest sortRequest,
            @RequestParam(required = false) Long slotId,
            @RequestParam(required = false) SeedbedSowingStatus status) {
        SeedbedSowingSearchCriteria criteria = new SeedbedSowingSearchCriteria(null, slotId, id, status);

        return new ApiResponseWrapper<>(seedbedSowingService.getAllSeedbedSowings(filterRequest, sortRequest, criteria));
    }

    @Operation(summary = "Get Seedbed Sowing by ID", description =
            "Finds a seedbed sowing by its ID, verifying that it belongs to the specified seedbed and slot")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Founded seedbed sowing"),
            @ApiResponse(responseCode = "404", description = "Seedbed sowing doesn't exist for the specified seedbed and slot")
    })
    @GetMapping("/{id}/slots/{slotId}/sowings/{sowingId}")
    public ApiResponseWrapper<SeedbedSowingResponse> getSeedbedSowingByIdAndSeedbedIdAndSlotId(
            @PathVariable Long id, @PathVariable Long slotId, @PathVariable Long sowingId) {
        return new ApiResponseWrapper<>(seedbedSowingService.getSeedbedSowingByIdAndSeedbedIdAndSlotId(sowingId, id, slotId));
    }

    @Operation(summary = "Creates a Seedbed Sowing", description =
            "Creates a new seedbed sowing in the specified seedbed slot, provided the slot has no active sowing")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Seedbed sowing created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "404", description = "Seedbed slot or seed packet does not exist"),
            @ApiResponse(responseCode = "409", description = "Seedbed slot already contains an active sowing")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{id}/slots/{slotId}/sowings")
    public ApiResponseWrapper<SeedbedSowingResponse> createSeedbedSowingBySeedbedIdAndSlotId(
            @PathVariable Long id, @PathVariable Long slotId, @RequestBody @Valid SeedbedSowingRequest request) {
        return new ApiResponseWrapper<>(seedbedSowingService.createSeedbedSowing(id, slotId, request));
    }

    @Operation(summary = "Update a Seedbed Sowing by ID", description =
            "Update an existing seedbed sowing by its ID, belonging to the specified seedbed and slot. " +
                    "Terminal sowings cannot be modified")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sowing updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "404", description = "Sowing or seed packet does not exist"),
            @ApiResponse(responseCode = "409", description = "sowing cannot be updated because status is terminal")
    })
    @PutMapping("/{id}/slots/{slotId}/sowings/{sowingId}")
    public ApiResponseWrapper<SeedbedSowingResponse> updateSeedbedSowingByIdAndSeedbedIdAndSlotId(
            @PathVariable Long id, @PathVariable Long slotId, @PathVariable Long sowingId,
            @RequestBody @Valid SeedbedSowingRequest request) {
        return new ApiResponseWrapper<>(seedbedSowingService.updateSeedbedSowingById(sowingId, id, slotId, request));
    }

    @Operation(summary = "Update a Seedbed Sowing Status by ID", description =
            "Update a seedbed sowing status by its ID and seedbed ID and slot ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Sowing status updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "404", description = "Sowing doesn't exist for the specified seedbed and slot"),
            @ApiResponse(responseCode = "409", description = "Invalid sowing status transition")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping("/{id}/slots/{slotId}/sowings/{sowingId}/status")
    public void updateSeedbedSowingStatusByIdAndSeedbedIdAndSlotId(
            @PathVariable Long id, @PathVariable Long slotId, @PathVariable Long sowingId,
            @RequestBody @Valid SeedbedSowingStatusRequest request) {
        seedbedSowingService.updateSeedbedSowingStatusById(sowingId, id, slotId, request);
    }
}
