package com.deploymentapi.dto;

import com.deploymentapi.model.DeploymentStatus;

public class DeploymentFilter {
    private String service;
    private DeploymentStatus status;

    public DeploymentFilter() {
    }

    public DeploymentFilter(String service, DeploymentStatus status) {
        this.service = service;
        this.status = status;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public DeploymentStatus getStatus() {
        return status;
    }

    public void setStatus(DeploymentStatus status) {
        this.status = status;
    }
}

