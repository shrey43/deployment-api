package com.deploymentapi;

import com.deploymentapi.dto.DeploymentFilter;
import com.deploymentapi.model.Deployment;
import com.deploymentapi.model.DeploymentStatus;
import com.deploymentapi.repository.InMemoryDeploymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryDeploymentRepositoryTest {

    private InMemoryDeploymentRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryDeploymentRepository();
    }

    @Test
    void findAll_ShouldReturnNewestFirst() {
        Deployment older = deployment("deploy_001", "billing-api", DeploymentStatus.SUCCESS,
                Instant.parse("2025-04-01T08:00:00Z"));
        Deployment newer = deployment("deploy_002", "billing-api", DeploymentStatus.FAILED,
                Instant.parse("2025-04-03T08:00:00Z"));

        repository.save(older);
        repository.save(newer);

        List<Deployment> results = repository.findAll();
        assertEquals(2, results.size());
        assertEquals("deploy_002", results.get(0).getId());
        assertEquals("deploy_001", results.get(1).getId());
    }

    @Test
    void findByFilter_ShouldMatchServiceCaseInsensitively() {
        repository.save(deployment("deploy_001", "billing-api", DeploymentStatus.SUCCESS,
                Instant.parse("2025-04-01T08:00:00Z")));

        List<Deployment> results = repository.findByFilter(new DeploymentFilter("Billing-API", null));

        assertEquals(1, results.size());
        assertEquals("deploy_001", results.get(0).getId());
    }

    @Test
    void findByFilter_ShouldFilterByStatus() {
        repository.save(deployment("deploy_001", "billing-api", DeploymentStatus.SUCCESS,
                Instant.parse("2025-04-01T08:00:00Z")));
        repository.save(deployment("deploy_002", "billing-api", DeploymentStatus.FAILED,
                Instant.parse("2025-04-02T08:00:00Z")));

        List<Deployment> results = repository.findByFilter(
                new DeploymentFilter(null, DeploymentStatus.FAILED));

        assertEquals(1, results.size());
        assertEquals(DeploymentStatus.FAILED, results.get(0).getStatus());
    }

    @Test
    void findById_ShouldReturnCopyNotSharedReference() {
        Deployment original = deployment("deploy_001", "billing-api", DeploymentStatus.SUCCESS,
                Instant.parse("2025-04-01T08:00:00Z"));
        repository.save(original);

        Deployment fetched = repository.findById("deploy_001").orElseThrow();
        fetched.setService("mutated");

        Deployment refetched = repository.findById("deploy_001").orElseThrow();
        assertEquals("billing-api", refetched.getService());
    }

    private Deployment deployment(String id, String service, DeploymentStatus status, Instant timestamp) {
        return new Deployment(id, service, status, 150, timestamp, "abc1234");
    }
}
