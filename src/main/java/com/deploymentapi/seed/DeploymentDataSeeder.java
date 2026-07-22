package com.deploymentapi.seed;

import com.deploymentapi.model.Deployment;
import com.deploymentapi.model.DeploymentStatus;
import com.deploymentapi.repository.DeploymentRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
public class DeploymentDataSeeder {
    private final DeploymentRepository repository;

    public DeploymentDataSeeder(DeploymentRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void seedData() {
        String[] services = {
                "billing-api",
                "user-service",
                "notification-service",
                "payment-gateway",
                "inventory-service",
                "api-gateway"
        };

        DeploymentStatus[] statuses = DeploymentStatus.values();
        String[] commitShas = {
                "a1b2c3d", "e4f5g6h", "i7j8k9l", "m0n1o2p", "q3r4s5t",
                "u6v7w8x", "y9z0a1b", "c2d3e4f", "g5h6i7j", "k8l9m0n"
        };

        Instant baseTime = Instant.parse("2025-04-01T08:00:00Z");

        // Create 35 deployment events
        for (int i = 0; i < 35; i++) {
            String id = String.format("deploy_%03d", i + 1);
            String service = services[i % services.length];
            DeploymentStatus status = statuses[i % statuses.length];
            int duration = 150 + (i * 17) % 400; // Varies between 150-550 seconds
            Instant timestamp = baseTime.plus(i * 2, ChronoUnit.DAYS).plus(i * 3, ChronoUnit.HOURS);
            String commitSha = commitShas[i % commitShas.length];

            Deployment deployment = new Deployment(id, service, status, duration, timestamp, commitSha);
            repository.save(deployment);
        }

        System.out.println("✓ Seeded 35 deployment events");
    }
}

