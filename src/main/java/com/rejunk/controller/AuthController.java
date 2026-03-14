package com.rejunk.controller;

import com.rejunk.domain.model.User;
import com.rejunk.dto.auth.AuthResponse;
import com.rejunk.dto.auth.LoginRequest;
import com.rejunk.dto.auth.RegisterRequest;
import com.rejunk.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    public AuthController(UserService userService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest request) {
        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPasswordHash(request.getPassword());

        User savedUser = userService.registerUser(user);

        return new AuthResponse(
                savedUser.getId().toString(),
                savedUser.getFullName(),
                savedUser.getEmail(),
                savedUser.getRole().name(),
                "User registered successfully"
        );
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userService.getUserByEmail(request.getEmail());

        return new AuthResponse(
                user.getId().toString(),
                user.getFullName(),
                user.getEmail(),
                user.getRole().name(),
                "Login successful"
        );
    }
}