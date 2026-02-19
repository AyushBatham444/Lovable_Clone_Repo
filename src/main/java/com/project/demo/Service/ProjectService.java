package com.project.demo.Service;

import com.project.demo.dto.project.ProjectRequest;
import com.project.demo.dto.project.ProjectResponse;
import com.project.demo.dto.project.ProjectSummaryResponse;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface ProjectService {
     List<ProjectSummaryResponse> getUserProjects();

     ProjectSummaryResponse getUserProjectById(Long id );

     ProjectResponse createProject(ProjectRequest request);

     ProjectResponse updateProject(Long id, ProjectRequest request);

    void softDelete(Long id);
}
