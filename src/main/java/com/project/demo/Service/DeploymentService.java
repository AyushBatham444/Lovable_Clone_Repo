package com.project.demo.Service;

import com.project.demo.dto.deploy.DeployResponse;

public interface DeploymentService  {

    DeployResponse deploy(Long projectId);
}
