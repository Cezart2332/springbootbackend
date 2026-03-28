package com.cezar.backend.dto.user;

import com.cezar.backend.entities.User;

public class UserResponse {
    public String name;
    public String email;

    public UserResponse(User user) {
        this.name = user.getName();
        this.email = user.getEmail();
    }
}
