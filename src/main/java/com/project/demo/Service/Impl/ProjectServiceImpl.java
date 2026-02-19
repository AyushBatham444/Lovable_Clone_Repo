package com.project.demo.Service.Impl;

import com.project.demo.Entity.Project;
import com.project.demo.Entity.ProjectMember;
import com.project.demo.Entity.User;
import com.project.demo.Entity.enums.ProjectMemberId;
import com.project.demo.Entity.enums.ProjectRole;
import com.project.demo.Service.ProjectService;
import com.project.demo.Service.ProjectTemplateService;
import com.project.demo.Service.SubscriptionService;
import com.project.demo.dto.project.ProjectRequest;
import com.project.demo.dto.project.ProjectResponse;
import com.project.demo.dto.project.ProjectSummaryResponse;
import com.project.demo.errors.BadRequestException;
import com.project.demo.errors.ResourceNotFoundException;
import com.project.demo.mapper.ProjectMapper;
import com.project.demo.repository.ProjectMemberRepository;
import com.project.demo.repository.ProjectRepository;
import com.project.demo.repository.UserRepository;
import com.project.demo.security.AuthUtil;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
@Transactional // so that in any error we can rollback (used after mapper here) , also it commits the changes to db by dirty changes
public class ProjectServiceImpl implements ProjectService {

    ProjectRepository projectRepositoryObj;
    UserRepository userRepositoryObj;
    ProjectMapper projectMapper;
    ProjectMemberRepository projectMemberRepositoryObj;
    AuthUtil authUtil;
    SubscriptionService subscriptionServiceObj;
    ProjectTemplateService projectTemplateService;

    @Override
    public ProjectResponse createProject(ProjectRequest request) {

        if(!subscriptionServiceObj.canCreateNewProject()) // u can create if you have not reached the limit to create them ie 1 project for free plan 3 for pro 10 for business
        {
            throw new BadRequestException("User cannot create a new project with current plan please upgrade the plan");
        }

        Long userId= authUtil.getCurrentUserId();
//        User owner=userRepositoryObj.findById(userId).orElseThrow(
//                ()-> new ResourceNotFoundException("User",userId.toString())
//        );
        User owner= userRepositoryObj.getReferenceById(userId);


        Project project=Project.builder()
                .name(request.name())
                .isPublic(false) // we need to specifically define false the default value we  defined in project as false wont work
                .build();



        project=projectRepositoryObj.save(project); // we cant use model mapper as it wont work for records (project response) so we will use map Struct
        // we use records as they are immutable and we dont need to write getter setter so it makes code less bulky

        ProjectMemberId projectMemberId=new ProjectMemberId(project.getId(), owner.getId());
        ProjectMember projectMember=ProjectMember.builder()
                .id(projectMemberId)
                .projectRole(ProjectRole.OWNER)
                .user(owner)
                .acceptedAt(Instant.now())
                .invitedAt(Instant.now())
                .project(project)
                .build();
        projectMemberRepositoryObj.save(projectMember);
        projectTemplateService.initializeProjectFromTemplate(project.getId()); // to attach the premade template with this project in minio as soon as the project is created

        return projectMapper.toProjectResponse(project);
    }

    @Override
    public List<ProjectSummaryResponse> getUserProjects() {
        Long userId= authUtil.getCurrentUserId();
        var projectsWithRoles=projectRepositoryObj.findAllAccessibleByUser(userId);
        return projectsWithRoles.stream()
                .map(p-> projectMapper.toProjectSummaryResponse(p.getProject(),p.getRole()))
                .toList();

//        return projectRepositoryObj.findAllAccessibleByUser(userId)
//                .stream()
//             .map(project -> projectMapper.toProjectSummaryResponse(project)) // this line is same as below converted to lambda by compiler
//                .map(projectMapper::toProjectSummaryResponse)
//                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("@security.canViewProject(#projectId)") // this is spel language
    public ProjectSummaryResponse getUserProjectById(Long projectId) { // here and below just id is project id
        Long userId= authUtil.getCurrentUserId();
        var projectWithRole=projectRepositoryObj.findAccessibleProjectByIdWithRole(projectId,userId)
                .orElseThrow(()-> new BadRequestException("Project Not Found"));
        return projectMapper.toProjectSummaryResponse(projectWithRole.getProject(),projectWithRole.getRole());
    }

    @Override
    @PreAuthorize("@security.canEditProject(#projectId)") // this is spel language
    public ProjectResponse updateProject(Long projectId, ProjectRequest request) {
        Long userId= authUtil.getCurrentUserId();
        Project project=getAccessibleProjectById(projectId,userId);



        project.setName(request.name());
        project=projectRepositoryObj.save(project);
        return projectMapper.toProjectResponse(project);
    }

    @Override
    @PreAuthorize("@security.canDeleteProject(#projectId)") // this is spel language
    public void softDelete(Long projectId) {
        Long userId= authUtil.getCurrentUserId();
        Project project=getAccessibleProjectById(projectId,userId);


        project.setDeletedAt(Instant.now());  // this is soft delete, if ever user want to take this project again then just set setDeletedAt to null
        projectRepositoryObj.save(project); // optional line since we have @transactional annotation
    }




    //// internal function
    public Project getAccessibleProjectById(Long projectId,Long userId)
    {
        return projectRepositoryObj.findAccessibleProjectById(projectId,userId)
                .orElseThrow(()-> new ResourceNotFoundException("Project",projectId.toString()));
    }
}
