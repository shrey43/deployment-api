package com.deploymentapi;

import com.deploymentapi.exception.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/deployments");
        webRequest = new ServletWebRequest(request);
    }

    @Test
    void handleDeploymentNotFoundException_ShouldReturn404() {
        ResponseEntity<Map<String, Object>> response = handler.handleDeploymentNotFoundException(
                new DeploymentNotFoundException("deploy_999"), webRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertErrorBody(response.getBody(), 404, "Not Found", "Deployment not found with id: deploy_999");
    }

    @Test
    void handleInvalidFilterException_ShouldReturn400() {
        ResponseEntity<Map<String, Object>> response = handler.handleInvalidFilterException(
                new InvalidFilterException("Service name cannot be empty"), webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertErrorBody(response.getBody(), 400, "Bad Request", "Service name cannot be empty");
    }

    @Test
    void handleInvalidDeploymentIdException_ShouldReturn400() {
        ResponseEntity<Map<String, Object>> response = handler.handleInvalidDeploymentIdException(
                new InvalidDeploymentIdException("Deployment ID cannot be empty"), webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertErrorBody(response.getBody(), 400, "Bad Request", "Deployment ID cannot be empty");
    }

    @Test
    void handleGlobalException_ShouldReturn500() {
        ResponseEntity<Map<String, Object>> response = handler.handleGlobalException(
                new RuntimeException("boom"), webRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertErrorBody(response.getBody(), 500, "Internal Server Error", "An unexpected error occurred");
    }

    private void assertErrorBody(Map<String, Object> body, int status, String error, String message) {
        assertNotNull(body);
        assertEquals(status, body.get("status"));
        assertEquals(error, body.get("error"));
        assertEquals(message, body.get("message"));
        assertNotNull(body.get("timestamp"));
        assertNotNull(body.get("path"));
    }
}
