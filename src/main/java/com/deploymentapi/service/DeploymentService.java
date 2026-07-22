package com.deploymentapi.service;

import com.deploymentapi.model.Deployment;

import java.util.List;

public interface DeploymentService {
    List<Deployment> getDeploymentsByFilter(String service, String status);
    Deployment getDeploymentById(String id);
}
