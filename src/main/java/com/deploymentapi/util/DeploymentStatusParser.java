package com.deploymentapi.util;

import com.deploymentapi.exception.InvalidFilterException;
import com.deploymentapi.model.DeploymentStatus;

import java.util.Arrays;
import java.util.stream.Collectors;

public final class DeploymentStatusParser {

    private DeploymentStatusParser() {
    }

    public static DeploymentStatus parse(String statusStr) {
        if (statusStr == null) {
            return null;
        }

        try {
            return DeploymentStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            String validValues = Arrays.stream(DeploymentStatus.values())
                    .map(Enum::name)
                    .collect(Collectors.joining(", "));
            throw new InvalidFilterException(
                    String.format("Invalid status value: '%s'. Allowed values: [%s]",
                            statusStr, validValues));
        }
    }
}
