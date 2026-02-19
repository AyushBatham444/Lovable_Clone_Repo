package com.project.demo.mapper;

import com.project.demo.Entity.Project;
import com.project.demo.Entity.enums.ProjectRole;
import com.project.demo.dto.project.ProjectResponse;
import com.project.demo.dto.project.ProjectSummaryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring") // specifically making component model as spring as Mapper work with even normal java code so its important to mention that its for spring
public interface ProjectMapper {

    ProjectResponse toProjectResponse(Project project); // just write the function definition that takes project type and sends back projectResponse rec

    ProjectSummaryResponse toProjectSummaryResponse(Project project, ProjectRole role);

    List<ProjectSummaryResponse> toListProjectSummaryResponse(List<Project> projects);
}

