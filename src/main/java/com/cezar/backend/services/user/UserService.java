package com.cezar.backend.services.user;

import com.cezar.backend.dto.user.UserRequest;
import com.cezar.backend.dto.user.UserResponse;
import com.cezar.backend.entities.User;
import com.cezar.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class UserService implements IUserService {
    private UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserResponse createUser(UserRequest userRequest) {

        String encodedPassword = passwordEncoder.encode(userRequest.getPassword());
        System.out.println(encodedPassword);
        User user = new User(userRequest.getName(), userRequest.getEmail(), encodedPassword);
        return new UserResponse(userRepository.save(user));
    }

    @Override
    public UserResponse loginUser(UserRequest userRequest) throws Exception {
        Optional<User> userInfo = userRepository.findByEmail(userRequest.getEmail());
        if(userInfo.isEmpty()) throw new Exception("User not found");
        User user = userInfo.get();
        if(passwordEncoder.matches(userRequest.getPassword(), user.getPassword())){
            return new UserResponse(user);
        }
        else{
            throw new Exception("Invalid password");
        }

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
