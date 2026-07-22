package com.deploymentapi;

import com.deploymentapi.dto.DeploymentFilter;
import com.deploymentapi.exception.DeploymentNotFoundException;
import com.deploymentapi.exception.InvalidFilterException;
import com.deploymentapi.model.Deployment;
import com.deploymentapi.model.DeploymentStatus;
import com.deploymentapi.repository.DeploymentRepository;
import com.deploymentapi.service.DeploymentService;
import com.deploymentapi.service.DeploymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeploymentServiceTest {

    @Mock
    private DeploymentRepository repository;

    private DeploymentService service;

    private Deployment testDeployment1;
    private Deployment testDeployment2;
    private Deployment testDeployment3;

    @BeforeEach
    void setUp() {
        service = new DeploymentServiceImpl(repository);

        testDeployment1 = new Deployment(
                "deploy_001",
                "billing-api",
                DeploymentStatus.SUCCESS,
                210,
                Instant.parse("2025-04-01T08:00:00Z"),
                "a1b2c3d"
        );

        testDeployment2 = new Deployment(
                "deploy_002",
                "user-service",
                DeploymentStatus.FAILED,
                320,
                Instant.parse("2025-04-02T10:00:00Z"),
                "e4f5g6h"
        );

        testDeployment3 = new Deployment(
                "deploy_003",
                "billing-api",
                DeploymentStatus.IN_PROGRESS,
                150,
                Instant.parse("2025-04-03T12:00:00Z"),
                "i7j8k9l"
        );
    }

    @Test
    void getAllDeployments_ShouldReturnAllDeployments() {
        // Arrange
        List<Deployment> expectedDeployments = Arrays.asList(testDeployment1, testDeployment2, testDeployment3);
        when(repository.findAll()).thenReturn(expectedDeployments);

        // Act
        List<Deployment> result = service.getAllDeployments();

        // Assert
        assertEquals(3, result.size());
        verify(repository, times(1)).findAll();
    }

    @Test
    void getDeploymentsByFilter_WithServiceFilter_ShouldReturnFilteredResults() {
        // Arrange
        DeploymentFilter filter = new DeploymentFilter("billing-api", null);
        List<Deployment> expectedDeployments = Arrays.asList(testDeployment1, testDeployment3);
        when(repository.findByFilter(any(DeploymentFilter.class))).thenReturn(expectedDeployments);

        // Act
        List<Deployment> result = service.getDeploymentsByFilter(filter);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(d -> d.getService().equals("billing-api")));
        verify(repository, times(1)).findByFilter(filter);
    }

    @Test
    void getDeploymentsByFilter_WithStatusFilter_ShouldReturnFilteredResults() {
        // Arrange
        DeploymentFilter filter = new DeploymentFilter(null, DeploymentStatus.FAILED);
        List<Deployment> expectedDeployments = List.of(testDeployment2);
        when(repository.findByFilter(any(DeploymentFilter.class))).thenReturn(expectedDeployments);

        // Act
        List<Deployment> result = service.getDeploymentsByFilter(filter);

        // Assert
        assertEquals(1, result.size());
        assertEquals(DeploymentStatus.FAILED, result.get(0).getStatus());
        verify(repository, times(1)).findByFilter(filter);
    }

    @Test
    void getDeploymentsByFilter_WithBothFilters_ShouldReturnFilteredResults() {
        // Arrange
        DeploymentFilter filter = new DeploymentFilter("billing-api", DeploymentStatus.SUCCESS);
        List<Deployment> expectedDeployments = List.of(testDeployment1);
        when(repository.findByFilter(any(DeploymentFilter.class))).thenReturn(expectedDeployments);

        // Act
        List<Deployment> result = service.getDeploymentsByFilter(filter);

        // Assert
        assertEquals(1, result.size());
        assertEquals("billing-api", result.get(0).getService());
        assertEquals(DeploymentStatus.SUCCESS, result.get(0).getStatus());
    }

    @Test
    void getDeploymentsByFilter_WithNoFilters_ShouldReturnAllDeployments() {
        // Arrange
        DeploymentFilter filter = new DeploymentFilter(null, null);
        List<Deployment> expectedDeployments = Arrays.asList(testDeployment1, testDeployment2, testDeployment3);
        when(repository.findAll()).thenReturn(expectedDeployments);

        // Act
        List<Deployment> result = service.getDeploymentsByFilter(filter);

        // Assert
        assertEquals(3, result.size());
        verify(repository, times(1)).findAll();
    }

    @Test
    void getDeploymentById_WithValidId_ShouldReturnDeployment() {
        // Arrange
        when(repository.findById("deploy_001")).thenReturn(Optional.of(testDeployment1));

        // Act
        Deployment result = service.getDeploymentById("deploy_001");

        // Assert
        assertNotNull(result);
        assertEquals("deploy_001", result.getId());
        assertEquals("billing-api", result.getService());
        verify(repository, times(1)).findById("deploy_001");
    }

    @Test
    void getDeploymentById_WithInvalidId_ShouldThrowException() {
        // Arrange
        when(repository.findById("invalid_id")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(DeploymentNotFoundException.class, () -> {
            service.getDeploymentById("invalid_id");
        });
        verify(repository, times(1)).findById("invalid_id");
    }

    @Test
    void getDeploymentById_WithNullId_ShouldThrowException() {
        // Act & Assert
        assertThrows(InvalidFilterException.class, () -> {
            service.getDeploymentById(null);
        });
        verify(repository, never()).findById(any());
    }

    @Test
    void getDeploymentById_WithEmptyId_ShouldThrowException() {
        // Act & Assert
        assertThrows(InvalidFilterException.class, () -> {
            service.getDeploymentById("   ");
        });
        verify(repository, never()).findById(any());
    }

    @Test
    void parseStatus_WithValidStatus_ShouldReturnEnum() {
        // Act & Assert
        assertEquals(DeploymentStatus.SUCCESS, DeploymentServiceImpl.parseStatus("success"));
        assertEquals(DeploymentStatus.FAILED, DeploymentServiceImpl.parseStatus("FAILED"));
        assertEquals(DeploymentStatus.IN_PROGRESS, DeploymentServiceImpl.parseStatus("in_progress"));
        assertEquals(DeploymentStatus.ROLLED_BACK, DeploymentServiceImpl.parseStatus("RoLLeD_BaCk"));
    }

    @Test
    void parseStatus_WithInvalidStatus_ShouldThrowException() {
        // Act & Assert
        InvalidFilterException exception = assertThrows(InvalidFilterException.class, () -> {
            DeploymentServiceImpl.parseStatus("invalid_status");
        });
        assertTrue(exception.getMessage().contains("Invalid status value"));
        assertTrue(exception.getMessage().contains("invalid_status"));
    }

    @Test
    void parseStatus_WithNull_ShouldReturnNull() {
        // Act & Assert
        assertNull(DeploymentServiceImpl.parseStatus(null));
    }

    @Test
    void getDeploymentsByFilter_WithEmptyServiceName_ShouldThrowException() {
        // Arrange
        DeploymentFilter filter = new DeploymentFilter("   ", null);

        // Act & Assert
        assertThrows(InvalidFilterException.class, () -> {
            service.getDeploymentsByFilter(filter);
        });
    }
}

