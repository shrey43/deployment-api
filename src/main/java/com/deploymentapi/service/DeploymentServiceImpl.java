package com.deploymentapi.service;

import com.deploymentapi.dto.DeploymentFilter;
import com.deploymentapi.exception.DeploymentNotFoundException;
import com.deploymentapi.exception.InvalidFilterException;
import com.deploymentapi.model.Deployment;
import com.deploymentapi.model.DeploymentStatus;
import com.deploymentapi.repository.DeploymentRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeploymentServiceImpl implements DeploymentService {
    private final DeploymentRepository repository;

    public DeploymentServiceImpl(DeploymentRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Deployment> getAllDeployments() {
        return repository.findAll();
    }

    @Override
    public List<Deployment> getDeploymentsByFilter(DeploymentFilter filter) {
        validateFilter(filter);

        if (filter.getService() == null && filter.getStatus() == null) {
            return repository.findAll();
        }

        return repository.findByFilter(filter);
    }

    @Override
    public Deployment getDeploymentById(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new InvalidFilterException("Deployment ID cannot be empty");
        }

        return repository.findById(id)
                .orElseThrow(() -> new DeploymentNotFoundException(id));
    }

    private void validateFilter(DeploymentFilter filter) {
        if (filter == null) {
            return;
        }

        if (filter.getService() != null && filter.getService().trim().isEmpty()) {
            throw new InvalidFilterException("Service name cannot be empty");
        }
    }

    public static DeploymentStatus parseStatus(String statusStr) {
        if (statusStr == null) {
            return null;
        }

        try {
            return DeploymentStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            String validValues = Arrays.stream(DeploymentStatus.values())
                    .map(Enum::name)
                    .collect(Collectors.joining(", "));
            throw new InvalidFilterException(
                    String.format("Invalid status value: '%s'. Allowed values: [%s]",
                            statusStr, validValues));
        }
    }
}

