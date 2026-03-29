package com.cezar.backend.services.user;

import com.cezar.backend.dto.user.UserResponse;

public interface IUserService {
    UserResponse getUserData(String email) throws Exception;

}
