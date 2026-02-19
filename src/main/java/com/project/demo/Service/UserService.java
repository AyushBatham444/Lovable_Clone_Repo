package com.project.demo.Service;

import com.project.demo.dto.auth.UserProfileResponse;

public interface UserService {
     UserProfileResponse getProfile(Long userId);
}
