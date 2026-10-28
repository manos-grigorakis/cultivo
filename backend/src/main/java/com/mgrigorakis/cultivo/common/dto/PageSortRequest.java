package com.mgrigorakis.cultivo.common.dto;


import org.springframework.data.domain.Sort;

public record PageSortRequest(String sortBy, String sortDirection) {
    public PageSortRequest {
        if(sortBy == null) {
            sortBy = "id";
        }

        if(sortDirection == null) {
            sortDirection = "asc";
        }
    }

    public Sort createSort(String defaultSortBy) {
        String field = sortBy == null ? defaultSortBy : sortBy;
        String direction = sortDirection == null ? "asc" : sortDirection;

        return Sort.by(Sort.Direction.fromString(direction), field);
    }
}
