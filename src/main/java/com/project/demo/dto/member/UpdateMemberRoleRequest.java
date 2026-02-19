package com.project.demo.dto.member;

import com.project.demo.Entity.enums.ProjectRole;
import jakarta.validation.constraints.NotNull;

public record UpdateMemberRoleRequest(
       @NotNull ProjectRole role
) {
}
