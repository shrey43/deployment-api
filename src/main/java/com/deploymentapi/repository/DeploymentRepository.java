package com.deploymentapi.repository;

import com.deploymentapi.dto.DeploymentFilter;
import com.deploymentapi.model.Deployment;

import java.util.List;
import java.util.Optional;

public interface DeploymentRepository {
    List<Deployment> findAll();
    List<Deployment> findByFilter(DeploymentFilter filter);
    Optional<Deployment> findById(String id);
    Deployment save(Deployment deployment);
}

