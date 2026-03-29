package com.cezar.backend.services.user;

import com.cezar.backend.dto.user.UserResponse;
import com.cezar.backend.entities.User;
import com.cezar.backend.repositories.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements IUserService{
    private final UserRepository userRepository;
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse getUserData(String email) throws Exception{
        Optional<User> userInfo = userRepository.findByEmail(email);
        if(userInfo.isEmpty()) throw new Exception("User not found");
        return new UserResponse(userInfo.get());
    }

}
