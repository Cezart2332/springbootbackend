package com.cezar.backend.services.user;

import com.cezar.backend.dto.user.UserRequest;
import com.cezar.backend.dto.user.UserResponse;
import com.cezar.backend.entities.User;
import com.cezar.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class UserService implements IUserService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserResponse createUser(UserRequest userRequest) {
        User user = new User(userRequest.getName(), userRequest.getEmail());
        return new UserResponse(userRepository.save(user));
    }

    @Override
    public List<UserResponse> getUsers()
    {
        return userRepository.findAll()
                .stream()
                .map(UserResponse::new)
                .collect(Collectors.toList());
    }
}
