package com.cezar.backend.services.auth;

import com.cezar.backend.dto.user.AuthResponse;
import com.cezar.backend.dto.user.LoginRequest;
import com.cezar.backend.dto.user.RefreshRequest;
import com.cezar.backend.dto.user.RegisterRequest;
import com.cezar.backend.entities.RefreshToken;
import com.cezar.backend.entities.Role;
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
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;

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
        User user = new User(userRequest.getName(), userRequest.getEmail(), encodedPassword, Role.USER);
        userRepository.save(user);
        return generateTokenPair(user.getEmail(),user.getRole());
    }

    @Override
    @Transactional
    public AuthResponse createUserAdmin(RegisterRequest userRequest) throws Exception{
        if(userRepository.existsByEmail(userRequest.getEmail())) throw new Exception("Email already exists");
        String encodedPassword = passwordEncoder.encode(userRequest.getPassword());
        User user = new User(userRequest.getName(), userRequest.getEmail(), encodedPassword, Role.ADMIN);
        userRepository.save(user);
        return generateTokenPair(user.getEmail(),user.getRole());
    }

    @Override
    @Transactional
    public AuthResponse loginUser(LoginRequest userRequest) throws Exception {
        Optional<User> userInfo = userRepository.findByEmail(userRequest.getEmail());
        if(userInfo.isEmpty()) throw new Exception("User not found");
        User user = userInfo.get();
        if(passwordEncoder.matches(userRequest.getPassword(), user.getPassword())){
            return generateTokenPair(user.getEmail(),user.getRole());
        }
        else{
            throw new Exception("Invalid password");
        }

    }

    @Transactional
    public AuthResponse refreshToken(RefreshRequest refreshRequest) {
        RefreshToken stored = refreshTokenRepository.findByToken(refreshRequest.getRefreshToken())
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (stored.getExpiresAt().isBefore(Instant.now())) {
            refreshTokenRepository.delete(stored);
            throw new RuntimeException("Refresh token expired, please login again");
        }
        User user = stored.getUser();
        // Rotate — delete old token and issue new pair
        refreshTokenRepository.delete(stored);
        return generateTokenPair(user.getEmail(), user.getRole());
    }


    @Transactional
    public AuthResponse generateTokenPair(String email,Role role) {
        String accessToken = jwtUtil.generateToken(email, role);
        Optional<User> user = userRepository.findByEmail(email);
        if(user.isEmpty()) throw new RuntimeException("User not found");

        // Delete old refresh token (one active token per user)
        refreshTokenRepository.deleteByUser(user.get());

        RefreshToken refreshToken = new RefreshToken(
                UUID.randomUUID().toString(),
                user.get(),
                Instant.now().plusMillis(refreshExpiration)
        );
        refreshTokenRepository.save(refreshToken);

        return new AuthResponse(accessToken, refreshToken.getToken());
    }
}
