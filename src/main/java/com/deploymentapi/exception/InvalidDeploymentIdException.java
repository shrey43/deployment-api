package com.deploymentapi.exception;

public class InvalidDeploymentIdException extends DeploymentApiException {
    public InvalidDeploymentIdException(String message) {
        super(message);
    }
}
