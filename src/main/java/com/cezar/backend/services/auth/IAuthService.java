package com.cezar.backend.services.auth;

import com.cezar.backend.dto.user.AuthResponse;
import com.cezar.backend.dto.user.LoginRequest;
import com.cezar.backend.dto.user.RefreshRequest;
import com.cezar.backend.dto.user.RegisterRequest;

public interface IAuthService {
    AuthResponse createUser(RegisterRequest userRequest) throws Exception;
    AuthResponse loginUser(LoginRequest userRequest) throws Exception;
    AuthResponse refreshToken(RefreshRequest refreshRequest) throws Exception;
}
