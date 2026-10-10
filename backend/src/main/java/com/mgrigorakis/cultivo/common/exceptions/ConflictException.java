package com.mgrigorakis.cultivo.common.exceptions;

import lombok.Getter;

import java.util.Map;

@Getter
public class ConflictException extends RuntimeException {
    private final Map<String, Object> details;
    private final String errorCode;

    public ConflictException(String message) {
        super(message);
        this.details = Map.of();
        this.errorCode = "";
    }

    public ConflictException(String message, Map<String, Object> details) {
        super(message);
        this.details = details != null ? Map.copyOf(details) : Map.of();
        this.errorCode = "";
    }

    public ConflictException(String message, Map<String, Object> details, String errorCode) {
        super(message);
        this.details = details != null ? Map.copyOf(details) : Map.of();
        this.errorCode = errorCode != null ? errorCode : "";
    }

    public ConflictException(String message, String errorCode) {
        super(message);
        this.details = Map.of();
        this.errorCode = errorCode != null ? errorCode : "";
    }
}
