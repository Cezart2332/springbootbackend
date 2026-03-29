package com.cezar.backend.controllers;

import com.cezar.backend.dto.user.UserResponse;
import com.cezar.backend.entities.User;
import com.cezar.backend.services.user.IUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    private IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<?> getUserData(@AuthenticationPrincipal String email){
        try
        {
            UserResponse response = userService.getUserData(email);
            return ResponseEntity.ok(response);
        }
        catch (Exception e)
        {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }
    
}
