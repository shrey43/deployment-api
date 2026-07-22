package com.deploymentapi.exception;

public class DeploymentApiException extends RuntimeException {
    public DeploymentApiException(String message) {
        super(message);
    }

    public DeploymentApiException(String message, Throwable cause) {
        super(message, cause);
    }
}

