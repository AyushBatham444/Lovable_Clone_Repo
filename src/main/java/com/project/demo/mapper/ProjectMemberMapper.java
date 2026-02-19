package com.project.demo.mapper;

import com.project.demo.Entity.ProjectMember;
import com.project.demo.Entity.User;
import com.project.demo.dto.member.MemberResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProjectMemberMapper  {

    @Mapping(target = "userId",source = "id")
    @Mapping(target = "role" ,constant = "OWNER")
    MemberResponse toProjectMemberResponseFromOwner(User owner);

    @Mapping(target = "userId",source = "user.id")
    @Mapping(target = "username" , source = "user.username")
    @Mapping(target = "name" , source = "user.name")
    @Mapping(target = "role" , source = "projectRole")
    MemberResponse toProjectMemberResponseFromMember(ProjectMember projectMember);


}
