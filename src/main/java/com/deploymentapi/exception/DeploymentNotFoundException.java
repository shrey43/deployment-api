package com.deploymentapi.exception;

public class DeploymentNotFoundException extends DeploymentApiException {
    public DeploymentNotFoundException(String id) {
        super("Deployment not found with id: " + id);
    }
}

