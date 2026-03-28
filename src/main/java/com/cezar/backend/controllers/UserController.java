package com.cezar.backend.controllers;

import com.cezar.backend.dto.user.UserRequest;
import com.cezar.backend.dto.user.UserResponse;
import com.cezar.backend.repositories.UserRepository;
import com.cezar.backend.entities.User;
import com.cezar.backend.services.user.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private IUserService userService;
    @PostMapping
    public UserResponse createUser(@RequestBody UserRequest request) {
        return userService.createUser(request);
    }
    @GetMapping
    public List<UserResponse> getUsers() {

        return userService.getUsers();
    }
}
