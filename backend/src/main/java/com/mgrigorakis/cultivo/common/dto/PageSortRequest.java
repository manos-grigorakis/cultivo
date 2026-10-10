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

    public Sort createSort() {
        return sortDirection.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
    }
}
