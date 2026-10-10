package com.mgrigorakis.cultivo.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Date;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponseWrapper<T>(
        String transaction,
        T data,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss.SSS")
        Date timestamp,
        ErrorResponse error
) {
    public ApiResponseWrapper {
        if (transaction == null) transaction = UUID.randomUUID().toString();
        if (timestamp == null) timestamp = new Date();
    }

    public ApiResponseWrapper(T data) {
        this(null, data, null, null);
    }

    public ApiResponseWrapper(ErrorResponse error) {
        this(null, null, null, error);
    }
}
