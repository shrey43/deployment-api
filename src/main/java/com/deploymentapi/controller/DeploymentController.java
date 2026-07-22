package com.deploymentapi.controller;

import com.deploymentapi.dto.DeploymentFilter;
import com.deploymentapi.dto.DeploymentListResponse;
import com.deploymentapi.dto.DeploymentResponse;
import com.deploymentapi.model.Deployment;
import com.deploymentapi.service.DeploymentService;
import com.deploymentapi.service.DeploymentServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/deployments")
public class DeploymentController {
    private final DeploymentService deploymentService;

    public DeploymentController(DeploymentService deploymentService) {
        this.deploymentService = deploymentService;
    }

    @GetMapping
    public ResponseEntity<DeploymentListResponse> listDeployments(
            @RequestParam(required = false) String service,
            @RequestParam(required = false) String status) {

        DeploymentFilter filter = new DeploymentFilter(
                service,
                DeploymentServiceImpl.parseStatus(status)
        );

        List<Deployment> deployments = deploymentService.getDeploymentsByFilter(filter);
        List<DeploymentResponse> responseList = deployments.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new DeploymentListResponse(responseList));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeploymentResponse> getDeploymentById(@PathVariable String id) {
        Deployment deployment = deploymentService.getDeploymentById(id);
        return ResponseEntity.ok(toResponse(deployment));
    }

    private DeploymentResponse toResponse(Deployment deployment) {
        return new DeploymentResponse(
                deployment.getId(),
                deployment.getService(),
                deployment.getStatus(),
                deployment.getDuration(),
                deployment.getTimestamp(),
                deployment.getCommitSha()
        );
    }
}

