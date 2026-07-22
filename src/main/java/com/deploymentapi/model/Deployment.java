package com.deploymentapi.model;

import java.time.Instant;
import java.util.Objects;

public class Deployment {
    private String id;
    private String service;
    private DeploymentStatus status;
    private Integer duration;
    private Instant timestamp;
    private String commitSha;

    public Deployment() {
    }

    public Deployment(String id, String service, DeploymentStatus status, Integer duration, Instant timestamp, String commitSha) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Deployment that = (Deployment) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Deployment{" +
                "id='" + id + '\'' +
                ", service='" + service + '\'' +
                ", status=" + status +
                ", duration=" + duration +
                ", timestamp=" + timestamp +
                ", commitSha='" + commitSha + '\'' +
                '}';
    }
}

