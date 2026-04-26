package com.web.messanger.controller;

import org.springframework.web.bind.annotation.RestController;

import com.web.messanger.model.User;
import com.web.messanger.service.UserService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@RequestMapping("/user")
@RestController
public class UserController {
    
    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public void register(@RequestBody User user) {
        
        service.saveUser(user);
    }
    
}
