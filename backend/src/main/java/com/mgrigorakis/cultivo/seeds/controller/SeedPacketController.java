package com.mgrigorakis.cultivo.seeds.controller;

import com.mgrigorakis.cultivo.common.dto.ApiResponseWrapper;
import com.mgrigorakis.cultivo.common.dto.PageFilterRequest;
import com.mgrigorakis.cultivo.common.dto.PageSortRequest;
import com.mgrigorakis.cultivo.seeds.dto.SeedPacketCreateRequest;
import com.mgrigorakis.cultivo.seeds.dto.SeedPacketResponse;
import com.mgrigorakis.cultivo.seeds.dto.SeedPacketUpdateRequest;
import com.mgrigorakis.cultivo.seeds.service.SeedPacketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Seed Packets")
@RequiredArgsConstructor
@RequestMapping("/api/v1/seed-packets")
@RestController
public class SeedPacketController {
    private final SeedPacketService seedPacketService;

    @Operation(summary = "Get All Seed Packets", description = "Lists all seed packets with pagination")
    @ApiResponse(responseCode = "200", description = "List of all seed packets with pagination")
    @GetMapping
    public ApiResponseWrapper<Page<SeedPacketResponse>> getAllSeedPackets(
            @ModelAttribute @Valid PageFilterRequest filterRequest,
            @ModelAttribute PageSortRequest sortRequest) {
        return new ApiResponseWrapper<>(seedPacketService.getAllSeedPackets(filterRequest, sortRequest));
    }

    @Operation(summary = "Get Seed Packet by ID", description = "Finds a seed packet by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Founded seed packet"),
            @ApiResponse(responseCode = "404", description = "Seed packet doesn't exist")
    })
    @GetMapping("/{id}")
    public ApiResponseWrapper<SeedPacketResponse> getSeedPacketById(@PathVariable Long id) {
        return new ApiResponseWrapper<>(seedPacketService.getSeedPacketById(id));
    }

    @Operation(summary = "Create a Seed Packet", description = "Create a new seed packet")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Seed packet created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "404", description = "Plant variety not found"),
            @ApiResponse(responseCode = "409", description = "Seed packet already exists by code, brand and variety")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ApiResponseWrapper<SeedPacketResponse> createSeedPacket(@RequestBody @Valid SeedPacketCreateRequest request) {
        return new ApiResponseWrapper<>(seedPacketService.createSeedPacket(request));
    }

    @Operation(summary = "Update a Seed Packet by ID", description = "Update an existing seed packet by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Seed packet updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "404", description = "Seed packet or plant variety not found"),
            @ApiResponse(responseCode = "409", description = "Seed packet already exists by code, brand and variety")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PutMapping("/{id}")
    public ApiResponseWrapper<SeedPacketResponse> createSeedPacket(@PathVariable Long id,
                                                                   @RequestBody @Valid SeedPacketUpdateRequest request) {
        return new ApiResponseWrapper<>(seedPacketService.updateSeedPacketById(id, request));
    }

    @Operation(summary = "Delete a Seed Packet by ID", description = "Deletes a seed packet by the provided id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Seed packet deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Seed packet doesn't exist"),
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteSeedPacketById(@PathVariable Long id) {
        seedPacketService.deleteSeedPacketById(id);
    }
}
