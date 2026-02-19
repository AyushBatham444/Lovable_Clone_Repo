package com.project.demo.Controller;

import com.project.demo.Entity.ProjectMember;
import com.project.demo.Service.ProjectMemberService;
import com.project.demo.dto.member.InviteMemberRequest;
import com.project.demo.dto.member.MemberResponse;
import com.project.demo.dto.member.UpdateMemberRoleRequest;
import com.project.demo.security.AuthUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/members")
@RequiredArgsConstructor
public class ProjectMemberController {

    private final ProjectMemberService projectMemberServiceObj;


    @GetMapping
    public ResponseEntity<List<MemberResponse>> getProjectMembers(@PathVariable Long projectId){

        return ResponseEntity.ok(projectMemberServiceObj.getProjectMembers(projectId));

    }

    @PostMapping
    public ResponseEntity<MemberResponse> inviteMember(
            @PathVariable Long projectId,
            @RequestBody @Valid InviteMemberRequest request
    ){

        return ResponseEntity.status(HttpStatus.CREATED).body(
                projectMemberServiceObj.inviteMember(projectId,request)
        );
    }

    @PatchMapping("/{memberId}")
    public ResponseEntity<MemberResponse> updateMemberRole(
            @PathVariable Long projectId,
            @PathVariable Long memberId,
            @RequestBody @Valid UpdateMemberRoleRequest request
    ){

        return ResponseEntity.ok(projectMemberServiceObj.updateMemberRole(projectId,memberId,request));
    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> deleteMemberRole(
            @PathVariable Long projectId,
            @PathVariable Long memberId

    ){

        projectMemberServiceObj.removeProjectMember(projectId,memberId);
        return ResponseEntity.noContent().build();
    }

}
