package com.cezar.backend.services.auth;

import com.cezar.backend.dto.user.AuthResponse;
import com.cezar.backend.dto.user.LoginRequest;
import com.cezar.backend.dto.user.RefreshRequest;
import com.cezar.backend.dto.user.RegisterRequest;
import com.cezar.backend.entities.RefreshToken;
import com.cezar.backend.entities.User;
import com.cezar.backend.repositories.RefreshTokenRepository;
import com.cezar.backend.repositories.UserRepository;
import com.cezar.backend.security.JwtUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;


@Service
public class AuthService implements IAuthService {
    private UserRepository userRepository;
    private RefreshTokenRepository refreshTokenRepository;
    private JwtUtil jwtUtil;
    private BCryptPasswordEncoder passwordEncoder;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    public AuthService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, RefreshTokenRepository refreshTokenRepository,JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    @Transactional
    public AuthResponse createUser(RegisterRequest userRequest) throws Exception{
        if(userRepository.existsByEmail(userRequest.getEmail())) throw new Exception("Email already exists");
        String encodedPassword = passwordEncoder.encode(userRequest.getPassword());
        System.out.println(encodedPassword);
        User user = new User(userRequest.getName(), userRequest.getEmail(), encodedPassword);
        userRepository.save(user);
        return generateTokenPair(user.getEmail());
    }

    @Override
    @Transactional
    public AuthResponse loginUser(LoginRequest userRequest) throws Exception {
        Optional<User> userInfo = userRepository.findByEmail(userRequest.getEmail());
        if(userInfo.isEmpty()) throw new Exception("User not found");
        User user = userInfo.get();
        if(passwordEncoder.matches(userRequest.getPassword(), user.getPassword())){
            return generateTokenPair(user.getEmail());
        }
        else{
            throw new Exception("Invalid password");
        }

    }

    @Transactional
    public AuthResponse refreshToken(RefreshRequest refreshRequest) throws Exception {
        RefreshToken stored = refreshTokenRepository.findByToken(refreshRequest.getRefreshToken())
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (stored.getExpiresAt().isBefore(Instant.now())) {
            refreshTokenRepository.delete(stored);
            throw new RuntimeException("Refresh token expired, please login again");
        }

        // Rotate — delete old token and issue new pair
        refreshTokenRepository.delete(stored);
        return generateTokenPair(stored.getEmail());
    }


    @Transactional
    public AuthResponse generateTokenPair(String email) {
        String accessToken = jwtUtil.generateToken(email);

        // Delete old refresh token (one active token per user)
        refreshTokenRepository.deleteByEmail(email);

        RefreshToken refreshToken = new RefreshToken(
                UUID.randomUUID().toString(),
                email,
                Instant.now().plusMillis(refreshExpiration)
        );
        refreshTokenRepository.save(refreshToken);

        return new AuthResponse(accessToken, refreshToken.getToken());
    }
}
