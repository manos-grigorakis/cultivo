package com.mgrigorakis.cultivo.seedbeds.controller;

import com.mgrigorakis.cultivo.common.dto.ApiResponseWrapper;
import com.mgrigorakis.cultivo.common.dto.PageFilterRequest;
import com.mgrigorakis.cultivo.common.dto.PageSortRequest;
import com.mgrigorakis.cultivo.seedbeds.dto.SeedbedSowingResponse;
import com.mgrigorakis.cultivo.seedbeds.dto.SeedbedSowingSearchCriteria;
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

@Tag(name = "Seedbed Sowings")
@RequiredArgsConstructor
@RequestMapping("/api/v1/seedbed-sowings")
@RestController
public class SeedbedSowingController {
    private final SeedbedSowingService seedbedSowingService;

    @Operation(summary = "Get All Seedbed Sowings", description =
            "Lists all seedbed sowings with pagination and optional filtering by criteria")
    @ApiResponse(responseCode = "200", description = "Paginated list of all seedbed sowing")
    @GetMapping
    public ApiResponseWrapper<Page<SeedbedSowingResponse>> getAllSeedbedSowings(
            @ModelAttribute @Valid PageFilterRequest filterRequest, @ModelAttribute PageSortRequest sortRequest,
            @ModelAttribute SeedbedSowingSearchCriteria criteria) {
        return new ApiResponseWrapper<>(seedbedSowingService.getAllSeedbedSowings(filterRequest, sortRequest, criteria));
    }

    @Operation(summary = "Get Seedbed Sowing by ID", description = "Finds a seedbed sowing by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Founded seedbed sowing"),
            @ApiResponse(responseCode = "404", description = "Sowing doesn't exist")
    })
    @GetMapping("/{id}")
    public ApiResponseWrapper<SeedbedSowingResponse> getSeedbedSowingById(@PathVariable Long id) {
        return new ApiResponseWrapper<>(seedbedSowingService.getSeedbedSowingById(id));
    }

    @Operation(summary = "Delete Seedbed Sowing by ID", description =
            "Deletes a sowing by its ID. Only sowings with SOWN status can be deleted")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Sowing deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Sowing doesn't exist"),
            @ApiResponse(responseCode = "409", description = "Sowing cannot be deleted because its status is not SOWN")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteSeedbedSowingById(@PathVariable Long id) {
        seedbedSowingService.deleteSeedbedSowingById(id);
    }
}
