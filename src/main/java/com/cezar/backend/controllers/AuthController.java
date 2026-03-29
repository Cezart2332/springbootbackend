package com.cezar.backend.controllers;

import com.cezar.backend.dto.user.AuthResponse;
import com.cezar.backend.dto.user.LoginRequest;
import com.cezar.backend.dto.user.RefreshRequest;
import com.cezar.backend.dto.user.RegisterRequest;
import com.cezar.backend.services.auth.IAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private IAuthService authService;

    public AuthController(IAuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.createUser(request);
            return ResponseEntity.status(201).body(response); // 201 Created
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // 400 Bad Request
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.loginUser(request);
            return ResponseEntity.ok(response); // 200 OK
        } catch (Exception e) {
            return ResponseEntity.status(401).body(e.getMessage()); // 401 Unauthorized
        }
    }
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshRequest refreshRequest) {
        try{
            AuthResponse response = authService.refreshToken(refreshRequest);
            return ResponseEntity.ok(response);
        }
        catch (Exception e)
        {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }
}
