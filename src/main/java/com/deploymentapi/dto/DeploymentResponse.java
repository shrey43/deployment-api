package com.deploymentapi.dto;

import com.deploymentapi.model.DeploymentStatus;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public record DeploymentResponse(
        String id,
        String service,
        DeploymentStatus status,
        Integer duration,
        Instant timestamp,
        @JsonProperty("commit_sha") String commitSha
) {
}

