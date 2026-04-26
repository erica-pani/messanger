package com.web.messanger.controller;

import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UIController {

  @GetMapping("/login")
  public String getLoginPage() {

    return "login";
  }

  @GetMapping("/")
  public String getChatPage(Model model, Principal principal) {
    model.addAttribute("username", principal.getName());
    return "groups";
  }

  @GetMapping("/login/failed")
  public String getLoginPageFailed(Model model) {

    model.addAttribute("error", "Login fehlgfeschlagen");
    return "login";
  }

  @GetMapping("/register")
  public String getRegisterPage() {

    return "registerMessanger";
  }
}
