package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User findOrCreateUser(Long userId, String username) {
        return userRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = new User();
                    user.setUserId(userId);
                    user.setUsername(username);
                    return userRepository.save(user);
                });
    }
}
