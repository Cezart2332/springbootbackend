package com.cezar.backend.services.user;

import com.cezar.backend.dto.user.UserResponse;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;

public interface IUserService {
    UserResponse getUserData(String email) throws Exception;

}
