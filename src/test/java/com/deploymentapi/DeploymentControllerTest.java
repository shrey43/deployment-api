package com.deploymentapi;

import com.deploymentapi.controller.DeploymentController;
import com.deploymentapi.dto.DeploymentListResponse;
import com.deploymentapi.exception.DeploymentNotFoundException;
import com.deploymentapi.exception.GlobalExceptionHandler;
import com.deploymentapi.exception.InvalidDeploymentIdException;
import com.deploymentapi.exception.InvalidFilterException;
import com.deploymentapi.model.Deployment;
import com.deploymentapi.model.DeploymentStatus;
import com.deploymentapi.service.DeploymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DeploymentController.class)
@Import(GlobalExceptionHandler.class)
class DeploymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DeploymentService deploymentService;

    private final Deployment sampleDeployment = new Deployment(
            "deploy_001",
            "billing-api",
            DeploymentStatus.SUCCESS,
            210,
            Instant.parse("2025-04-01T08:00:00Z"),
            "a1b2c3d"
    );

    @Test
    void listDeployments_ShouldReturn200WithDataAndCount() throws Exception {
        when(deploymentService.getDeploymentsByFilter(null, null))
                .thenReturn(List.of(sampleDeployment));

        mockMvc.perform(get("/api/v1/deployments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count", is(1)))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].id", is("deploy_001")))
                .andExpect(jsonPath("$.data[0].service", is("billing-api")))
                .andExpect(jsonPath("$.data[0].status", is("SUCCESS")))
                .andExpect(jsonPath("$.data[0].commit_sha", is("a1b2c3d")));
    }

    @Test
    void listDeployments_WithFilters_ShouldPassParamsToService() throws Exception {
        when(deploymentService.getDeploymentsByFilter("billing-api", "failed"))
                .thenReturn(List.of(sampleDeployment));

        mockMvc.perform(get("/api/v1/deployments")
                        .param("service", "billing-api")
                        .param("status", "failed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count", is(1)));
    }

    @Test
    void listDeployments_WithInvalidStatus_ShouldReturn400() throws Exception {
        when(deploymentService.getDeploymentsByFilter(any(), eq("badvalue")))
                .thenThrow(new InvalidFilterException(
                        "Invalid status value: 'badvalue'. Allowed values: [SUCCESS, FAILED, IN_PROGRESS, ROLLED_BACK]"));

        mockMvc.perform(get("/api/v1/deployments").param("status", "badvalue"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.message", containsString("Invalid status value")));
    }

    @Test
    void getDeploymentById_WithValidId_ShouldReturn200() throws Exception {
        when(deploymentService.getDeploymentById("deploy_001")).thenReturn(sampleDeployment);

        mockMvc.perform(get("/api/v1/deployments/deploy_001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("deploy_001")))
                .andExpect(jsonPath("$.service", is("billing-api")));
    }

    @Test
    void getDeploymentById_WithUnknownId_ShouldReturn404() throws Exception {
        when(deploymentService.getDeploymentById("unknown_id"))
                .thenThrow(new DeploymentNotFoundException("unknown_id"));

        mockMvc.perform(get("/api/v1/deployments/unknown_id"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", containsString("unknown_id")));
    }

}
