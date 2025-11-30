package com.rag.chat.storage.service;

import com.rag.chat.storage.entity.UserEntity;
import com.rag.chat.storage.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserEntity createUser(String name) {
        UserEntity user = new UserEntity();
        user.setName(name);
        user.setApi_key_hash("API Key Hash");

        return userRepository.save(user);
    }

    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }
}
