package com.web.messanger.controller;

import com.web.messanger.controller.mapper.UserMapper;
import com.web.messanger.model.User;
import com.web.messanger.model.UserPrincipal;
import com.web.messanger.model.dtos.UserDTO;
import com.web.messanger.service.UserService;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
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

  @GetMapping("/me")
  public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal UserPrincipal user) {

    if (user == null) {
      return ResponseEntity.status(401).body("Not logged in");
    }

    return ResponseEntity.ok(Map.of("username", user.getUsername()));
  }

  @PostMapping("/register")
  public ResponseEntity<UserDTO> register(@Valid @RequestBody UserDTO userdto) {
    User user = service.saveUser(userdto);
    return new ResponseEntity<>(userMapper.toUserDTO(user), HttpStatus.CREATED);
  }
}
