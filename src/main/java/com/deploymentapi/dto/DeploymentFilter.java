package com.deploymentapi.dto;

import com.deploymentapi.model.DeploymentStatus;

public record DeploymentFilter(
        String service,
        DeploymentStatus status
) {
}

