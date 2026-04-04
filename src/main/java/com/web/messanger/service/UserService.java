package com.web.messanger.service;

import java.util.ArrayList;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.web.messanger.model.User;
import com.web.messanger.repos.UserRepository;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder encoder;

    public void saveUser(User user) {
        user.setHashed_password(encoder.encode(user.getHashed_password()));
        user.setMessages(new ArrayList<>());
        user.setGroups(new HashSet<>());
        userRepository.save(user);
    }
}
