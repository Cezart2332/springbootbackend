package com.cezar.backend.services.admin;

import com.cezar.backend.dto.user.UserResponse;

import java.util.List;

public interface IAdminService {

    List<UserResponse> getUsers();
}
