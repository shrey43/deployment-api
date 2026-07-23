package com.deploymentapi.seed;

import com.deploymentapi.model.Deployment;
import com.deploymentapi.model.DeploymentStatus;
import com.deploymentapi.repository.DeploymentRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;

@Component
public class DeploymentDataSeeder {
    private static final Logger log = LoggerFactory.getLogger(DeploymentDataSeeder.class);
    private static final long SEED = 42L;

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
        Random random = new Random(SEED);

        Instant baseTime = Instant.now().minus(30, ChronoUnit.DAYS);

        for (int i = 0; i < 60; i++) {
            String id = String.format("deploy_%03d", i + 1);
            String service = services[i % services.length];
            DeploymentStatus status = statuses[random.nextInt(statuses.length)];
            int duration = 150 + (i * 17) % 400;
            Instant timestamp = baseTime.plus(i * 12L, ChronoUnit.HOURS);
            String commitSha = generateRandomCommitSha(random);

            Deployment deployment = new Deployment(id, service, status, duration, timestamp, commitSha);
            repository.save(deployment);
        }

        log.info("Seeded 60 deployment events spread over 30 days");
    }

    private String generateRandomCommitSha(Random random) {
        String chars = "0123456789abcdef";
        StringBuilder sha = new StringBuilder(7);
        for (int i = 0; i < 7; i++) {
            sha.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sha.toString();
    }
}
