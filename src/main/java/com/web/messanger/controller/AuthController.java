package com.web.messanger.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.web.messanger.model.User;
import com.web.messanger.service.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@RequestMapping("/user")
@RestController
public class AuthController {
    
    @Autowired
    private AuthService service;

    @PostMapping("/register")
    public void register(@RequestBody User user) {
        
        service.register(user.getFirstname(), user.getLastname(), user.getUsername(), user.getHashed_password(), user.getBirthDate());
    }
    
}
