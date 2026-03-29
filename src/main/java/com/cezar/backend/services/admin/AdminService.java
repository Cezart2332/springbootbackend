package com.cezar.backend.services.admin;

import com.cezar.backend.dto.user.UserResponse;
import com.cezar.backend.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService implements IAdminService{
    private final UserRepository userRepository;

    public AdminService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Override
    public List<UserResponse> getUsers()
    {
        return userRepository.findAll().stream().map(UserResponse::new).toList();
    }
}
