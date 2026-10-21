package com.web.messanger.controller;

import com.web.messanger.controller.mapper.UserMapper;
import com.web.messanger.model.User;
import com.web.messanger.model.dtos.UserDTO;
import com.web.messanger.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/user")
@RestController
public class UserController {

  private final UserService service;
  private final UserMapper userMapper;

  public UserController(UserService service, UserMapper userMapper) {
    this.service = service;
    this.userMapper = userMapper;
  }

  @PostMapping("/register")
  public ResponseEntity<UserDTO> register(@RequestBody UserDTO userdto) {
    User user = service.saveUser(userdto);
    return new ResponseEntity<>(userMapper.toUserDTO(user), HttpStatus.CREATED);
  }
}
