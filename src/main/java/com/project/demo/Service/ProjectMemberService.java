package com.project.demo.Service;

import com.project.demo.dto.member.InviteMemberRequest;
import com.project.demo.dto.member.MemberResponse;
import com.project.demo.dto.member.UpdateMemberRoleRequest;

import java.util.List;

public interface ProjectMemberService {
     List<MemberResponse> getProjectMembers(Long projectId);

     MemberResponse inviteMember(Long projectId, InviteMemberRequest request);

    MemberResponse updateMemberRole(Long projectId, Long memberId, UpdateMemberRoleRequest request);

    void removeProjectMember(Long projectId, Long memberId);
}
