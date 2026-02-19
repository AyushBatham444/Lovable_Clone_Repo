package com.project.demo.security;

import com.project.demo.Entity.enums.ProjectPermission;
import com.project.demo.Entity.enums.ProjectRole;
import com.project.demo.repository.ProjectMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("security")
@RequiredArgsConstructor
public class SecurityExpressions {

    private final ProjectMemberRepository projectMemberRepositoryObj;
    private final AuthUtil authUtil;

    private boolean hasPermission(Long projectId,ProjectPermission projectPermission)
    {
        Long userId=authUtil.getCurrentUserId();

        return projectMemberRepositoryObj.findRoleByProjectIdAndUserId(projectId,userId)
                .map(role -> role.getPermission().contains(projectPermission))
                .orElse(false); // It will be true of any of above is true else it will be false
    }

    public boolean canViewProject(Long projectId)
    {
        return hasPermission(projectId,ProjectPermission.VIEW);
    }

    public boolean canEditProject(Long projectId)
    {
        return hasPermission(projectId,ProjectPermission.EDIT);
    }

    public boolean canDeleteProject(Long projectId)
    {
        return hasPermission(projectId,ProjectPermission.DELETE);
    }

    public boolean canViewMembers(Long projectId)
    {
        return hasPermission(projectId,ProjectPermission.VIEW_MEMBERS);
    }

    public boolean canManageMembers(Long projectId)
    {
        return hasPermission(projectId,ProjectPermission.MANAGE_MEMBERS);
    }
}
