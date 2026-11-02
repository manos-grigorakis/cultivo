package com.mgrigorakis.cultivo.plants.dto;

public record PlantSearchCriteria(
        Boolean archived,
        Long plantSpeciesId,
        Long plantVarietyId
) {}
