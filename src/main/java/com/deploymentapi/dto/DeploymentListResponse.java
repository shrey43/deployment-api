package com.deploymentapi.dto;

import java.util.List;

public record DeploymentListResponse(
        List<DeploymentResponse> data,
        int count
) {
    public DeploymentListResponse(List<DeploymentResponse> data) {
        this(data, data.size());
    }
}

