package com.mgrigorakis.cultivo.common.dto;


import org.springframework.data.domain.Sort;

public record PageSortRequest(String sortBy, String sortDirection) {
    public Sort createSort(String defaultSortBy) {
        return createSort(defaultSortBy, Sort.Direction.ASC);
    }

    public Sort createSort(String defaultSortBy, Sort.Direction defaultDirection) {
        String field = sortBy == null ? defaultSortBy : sortBy;
        Sort.Direction direction = sortDirection == null ? defaultDirection : Sort.Direction.fromString(sortDirection);


        return Sort.by(direction, field);
    }

}
