package com.cezar.backend.dto.user;

import com.cezar.backend.entities.Role;
import com.cezar.backend.entities.User;

public class UserResponse {
    public String name;
    public String email;
    public Role role;

    public UserResponse(User user) {
        this.name = user.getName();
        this.email = user.getEmail();
        this.role = user.getRole();
    }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public Role getRole() { return role; }
}
