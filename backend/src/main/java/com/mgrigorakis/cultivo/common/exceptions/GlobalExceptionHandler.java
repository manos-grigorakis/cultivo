package com.mgrigorakis.cultivo.common.exceptions;

import com.mgrigorakis.cultivo.common.dto.ApiResponseWrapper;
import com.mgrigorakis.cultivo.common.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    // Field Validation - 400
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseWrapper<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> details = new HashMap<>();

        ex.getBindingResult().getFieldErrors()
                .forEach(error -> details.put(error.getField(), error.getDefaultMessage())
                );

        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Field Validation Failed",
                null,
                details
        );

        return new ResponseEntity<>(new ApiResponseWrapper<>(response), HttpStatus.BAD_REQUEST);
    }

    // Not Found - 404
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponseWrapper<Void>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ErrorResponse response = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                null,
                null
        );

        return new ResponseEntity<>(new ApiResponseWrapper<>(response), HttpStatus.NOT_FOUND);
    }

    // Server error - 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseWrapper<Void>> handleException(Exception ex) {
        ErrorResponse response = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred",
                null,
                null
        );

        log.error("An unexpected error occurred", ex);
        return new ResponseEntity<>(new ApiResponseWrapper<>(response), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
