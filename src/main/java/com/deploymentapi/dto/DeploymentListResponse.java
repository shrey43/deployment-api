package com.deploymentapi.dto;

import java.util.List;

public class DeploymentListResponse {
    private List<DeploymentResponse> data;
    private int count;

    public DeploymentListResponse() {
    }

    public DeploymentListResponse(List<DeploymentResponse> data) {
        this.data = data;
        this.count = data.size();
    }

    public List<DeploymentResponse> getData() {
        return data;
    }

    public void setData(List<DeploymentResponse> data) {
        this.data = data;
        this.count = data != null ? data.size() : 0;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}

