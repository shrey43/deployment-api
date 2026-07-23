package com.deploymentapi.service;

import com.deploymentapi.dto.DeploymentFilter;
import com.deploymentapi.exception.DeploymentNotFoundException;
import com.deploymentapi.exception.InvalidDeploymentIdException;
import com.deploymentapi.exception.InvalidFilterException;
import com.deploymentapi.model.Deployment;
import com.deploymentapi.model.DeploymentStatus;
import com.deploymentapi.repository.DeploymentRepository;
import com.deploymentapi.util.DeploymentStatusParser;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeploymentServiceImpl implements DeploymentService {
    private final DeploymentRepository repository;

    public DeploymentServiceImpl(DeploymentRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Deployment> getDeploymentsByFilter(String service, String status) {
        DeploymentStatus parsedStatus = DeploymentStatusParser.parse(status);
        String normalizedService = normalizeService(service);
        return findByFilter(new DeploymentFilter(normalizedService, parsedStatus));
    }

    @Override
    public Deployment getDeploymentById(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new InvalidDeploymentIdException("Deployment ID cannot be empty");
        }

        return repository.findById(id)
                .orElseThrow(() -> new DeploymentNotFoundException(id));
    }

    private List<Deployment> findByFilter(DeploymentFilter filter) {
        if (filter.service() == null && filter.status() == null) {
            return repository.findAll();
        }

        return repository.findByFilter(filter);
    }

    private String normalizeService(String service) {
        if (service == null) {
            return null;
        }

        String trimmed = service.trim();
        if (trimmed.isEmpty()) {
            throw new InvalidFilterException("Service name cannot be empty");
        }

        return trimmed;
    }
}
