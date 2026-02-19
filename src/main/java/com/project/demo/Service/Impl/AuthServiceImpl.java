package com.project.demo.Service.Impl;

import com.project.demo.Entity.User;
import com.project.demo.Service.AuthService;
import com.project.demo.dto.auth.AuthResponse;
import com.project.demo.dto.auth.LoginRequest;
import com.project.demo.dto.auth.SignupRequest;
import com.project.demo.errors.BadRequestException;
import com.project.demo.mapper.UserMapper;
import com.project.demo.repository.UserRepository;
import com.project.demo.security.AuthUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

// these implement are made so that we can test these functionality (function) anywhere without changing the actual code in Service files (ex-testing,controller testing etc)
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
public class AuthServiceImpl implements AuthService {

    UserRepository userRepositoryObj;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    AuthUtil authUtil;
    AuthenticationManager authenticationManager;

    @Override
    public AuthResponse signup(SignupRequest request) {

        userRepositoryObj.findByUsername(request.username()).ifPresent(user->
        {
                throw new BadRequestException("User already exists with username: "+request.username());
        });

        User user=userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.password()));
        user=userRepositoryObj.save(user);

        String token=authUtil.generateAccessToken(user);

        return new AuthResponse(token,userMapper.toUserProfileResponse(user));
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        Authentication authentication=authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(),request.password())
        );
        User user=(User) authentication.getPrincipal();
        String token=authUtil.generateAccessToken(user);

        return new AuthResponse(token,userMapper.toUserProfileResponse(user));
    }
}
