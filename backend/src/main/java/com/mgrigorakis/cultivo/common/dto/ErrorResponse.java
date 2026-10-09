package com.mgrigorakis.cultivo.common.dto;


public record ErrorResponse(int status, String message, String errorCode, Object details) {
}
