package com.deploymentapi.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(DeploymentNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleDeploymentNotFoundException(
            DeploymentNotFoundException ex, WebRequest request) {
        log.warn("Deployment not found: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(InvalidFilterException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidFilterException(
            InvalidFilterException ex, WebRequest request) {
        log.warn("Invalid filter: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(InvalidDeploymentIdException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidDeploymentIdException(
            InvalidDeploymentIdException ex, WebRequest request) {
        log.warn("Invalid deployment ID: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex, WebRequest request) {
        log.warn("Malformed request body: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Malformed JSON request", request);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException ex, WebRequest request) {
        log.warn("Method not supported: {}", ex.getMethod());
        String message = "Method " + ex.getMethod() + " not supported for this endpoint";
        return buildErrorResponse(HttpStatus.METHOD_NOT_ALLOWED, message, request);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> handleMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex, WebRequest request) {
        log.warn("Unsupported media type: {}", ex.getContentType());
        return buildErrorResponse(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                "Unsupported media type. Use application/json", request);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNoHandlerFound(
            NoHandlerFoundException ex, WebRequest request) {
        log.warn("Endpoint not found: {}", ex.getRequestURL());
        return buildErrorResponse(HttpStatus.NOT_FOUND,
                "Endpoint not found: " + ex.getRequestURL(), request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(
            Exception ex, WebRequest request) {
        log.error("Unexpected error processing request", ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred", request);
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(
            HttpStatus status, String message, WebRequest request) {
        Map<String, Object> errorResponse = new LinkedHashMap<>();
        errorResponse.put("status", status.value());
        errorResponse.put("error", status.getReasonPhrase());
        errorResponse.put("message", message);
        errorResponse.put("timestamp", Instant.now().toString());
        errorResponse.put("path", request.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(errorResponse, status);
    }
}
