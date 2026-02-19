package com.project.demo.Service;

import com.project.demo.dto.auth.AuthResponse;
import com.project.demo.dto.auth.LoginRequest;
import com.project.demo.dto.auth.SignupRequest;

public interface AuthService {
    AuthResponse signup(SignupRequest request);


     AuthResponse login(LoginRequest request);
}
