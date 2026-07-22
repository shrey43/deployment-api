package com.deploymentapi.repository;

import com.deploymentapi.dto.DeploymentFilter;
import com.deploymentapi.model.Deployment;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class InMemoryDeploymentRepository implements DeploymentRepository {
    private final Map<String, Deployment> deployments = new ConcurrentHashMap<>();

    @Override
    public List<Deployment> findAll() {
        return deployments.values().stream()
                .sorted(timestampDescending())
                .map(this::copyOf)
                .collect(Collectors.toList());
    }

    @Override
    public List<Deployment> findByFilter(DeploymentFilter filter) {
        return deployments.values().stream()
                .filter(deployment -> matchesFilter(deployment, filter))
                .sorted(timestampDescending())
                .map(this::copyOf)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Deployment> findById(String id) {
        return Optional.ofNullable(deployments.get(id)).map(this::copyOf);
    }

    @Override
    public Deployment save(Deployment deployment) {
        if (deployment == null) {
            throw new IllegalArgumentException("Deployment cannot be null");
        }
        if (deployment.getId() == null || deployment.getId().isBlank()) {
            throw new IllegalArgumentException("Deployment ID cannot be null or blank");
        }

        deployments.put(deployment.getId(), copyOf(deployment));
        return copyOf(deployment);
    }

    private boolean matchesFilter(Deployment deployment, DeploymentFilter filter) {
        if (filter.service() != null) {
            String service = deployment.getService();
            if (service == null || !service.equalsIgnoreCase(filter.service())) {
                return false;
            }
        }
        if (filter.status() != null && deployment.getStatus() != filter.status()) {
            return false;
        }
        return true;
    }

    private Comparator<Deployment> timestampDescending() {
        return Comparator.comparing(
                Deployment::getTimestamp,
                Comparator.nullsLast(Comparator.reverseOrder())
        );
    }

    private Deployment copyOf(Deployment deployment) {
        return new Deployment(
                deployment.getId(),
                deployment.getService(),
                deployment.getStatus(),
                deployment.getDuration(),
                deployment.getTimestamp(),
                deployment.getCommitSha()
        );
    }
}
