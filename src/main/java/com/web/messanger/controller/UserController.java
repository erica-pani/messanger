package com.web.messanger.controller;

import com.web.messanger.model.User;
import com.web.messanger.model.UserPrincipal;
import com.web.messanger.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;


@RequestMapping("/user")
@RestController
public class UserController {

  private final UserService service;

  public UserController(UserService service) {
    this.service = service;
  }

  @GetMapping("/me")
  public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal UserPrincipal user) {

    if (user == null) {
        return ResponseEntity.status(401).body("Not logged in");
    }

    return ResponseEntity.ok(
      Map.of(
        "username", user.getUsername()
      )
    );
  }
  

  @PostMapping("/register")
  public void register(@RequestBody User user) {

    service.saveUser(user);
  }
}
