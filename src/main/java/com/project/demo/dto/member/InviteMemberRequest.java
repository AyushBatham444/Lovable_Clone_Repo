package com.project.demo.dto.member;

import com.project.demo.Entity.enums.ProjectRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record InviteMemberRequest(
       @Email @NotBlank String username,
       @NotNull ProjectRole role
) {
}
