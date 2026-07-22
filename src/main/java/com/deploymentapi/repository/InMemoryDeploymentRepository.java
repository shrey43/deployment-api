package com.deploymentapi.repository;

import com.deploymentapi.dto.DeploymentFilter;
import com.deploymentapi.model.Deployment;
import org.springframework.stereotype.Repository;

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
                .sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Deployment> findByFilter(DeploymentFilter filter) {
        return deployments.values().stream()
                .filter(deployment -> matchesFilter(deployment, filter))
                .sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Deployment> findById(String id) {
        return Optional.ofNullable(deployments.get(id));
    }

    @Override
    public Deployment save(Deployment deployment) {
        deployments.put(deployment.getId(), deployment);
        return deployment;
    }

    private boolean matchesFilter(Deployment deployment, DeploymentFilter filter) {
        if (filter.service() != null &&
                !deployment.getService().equalsIgnoreCase(filter.service())) {
            return false;
        }
        if (filter.status() != null &&
                deployment.getStatus() != filter.status()) {
            return false;
        }
        return true;
    }
}

