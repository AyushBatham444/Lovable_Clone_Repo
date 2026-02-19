package com.project.demo.mapper;

import com.project.demo.Entity.User;
import com.project.demo.dto.auth.SignupRequest;
import com.project.demo.dto.auth.UserProfileResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(SignupRequest signupRequest);

    UserProfileResponse toUserProfileResponse(User user);
}
