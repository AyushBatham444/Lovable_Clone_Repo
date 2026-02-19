package com.project.demo.Controller;

import com.project.demo.Service.AuthService;
import com.project.demo.Service.UserService;
import com.project.demo.dto.auth.AuthResponse;
import com.project.demo.dto.auth.LoginRequest;
import com.project.demo.dto.auth.SignupRequest;
import com.project.demo.dto.auth.UserProfileResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor // generate objects using constructor now you dont need to write them
@RequestMapping("/api/auth")
@FieldDefaults(makeFinal = true ,level = AccessLevel.PRIVATE)
public class AuthController {

     AuthService authServiceObj; // from service folder
     UserService userServiceObj;
    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@RequestBody SignupRequest request)
    {
        return ResponseEntity.ok(authServiceObj.signup(request));
    }


    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request)
    {
        return  ResponseEntity.ok(authServiceObj.login(request));
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getProfile(){
        Long userId=1L;
        return ResponseEntity.ok(userServiceObj.getProfile(userId));
    }
}
