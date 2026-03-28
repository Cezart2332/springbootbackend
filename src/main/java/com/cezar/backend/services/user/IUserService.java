package com.cezar.backend.services.user;

import com.cezar.backend.dto.user.UserRequest;
import com.cezar.backend.dto.user.UserResponse;

import java.util.List;

public interface IUserService {
    UserResponse createUser(UserRequest userRequest);
    UserResponse loginUser(UserRequest userRequest) throws Exception;
    List<UserResponse> getUsers();
}
