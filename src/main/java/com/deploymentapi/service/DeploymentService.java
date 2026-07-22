package com.deploymentapi.service;

import com.deploymentapi.dto.DeploymentFilter;
import com.deploymentapi.model.Deployment;

import java.util.List;

public interface DeploymentService {
    List<Deployment> getAllDeployments();
    List<Deployment> getDeploymentsByFilter(DeploymentFilter filter);
    Deployment getDeploymentById(String id);
}

