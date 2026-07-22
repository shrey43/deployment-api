package com.deploymentapi.dto;

import com.deploymentapi.model.DeploymentStatus;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public class DeploymentResponse {
    private String id;
    private String service;
    private DeploymentStatus status;
    private Integer duration;
    private Instant timestamp;

    @JsonProperty("commit_sha")
    private String commitSha;

    public DeploymentResponse() {
    }

    public DeploymentResponse(String id, String service, DeploymentStatus status, Integer duration, Instant timestamp, String commitSha) {
        this.id = id;
        this.service = service;
        this.status = status;
        this.duration = duration;
        this.timestamp = timestamp;
        this.commitSha = commitSha;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getCommitSha() {
        return commitSha;
    }

    public void setCommitSha(String commitSha) {
        this.commitSha = commitSha;
    }
}

