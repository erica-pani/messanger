package com.web.messanger.controller;

import com.web.messanger.model.dtos.UserDTO;
import com.web.messanger.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/user")
@RestController
public class UserController {

  private final UserService service;

  public UserController(UserService service) {
    this.service = service;
  }

  @PostMapping("/register")
  public void register(@RequestBody UserDTO user) {

    service.saveUser(user);
  }
}
