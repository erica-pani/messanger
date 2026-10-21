package com.web.messanger.service;

import com.web.messanger.model.User;
import com.web.messanger.model.dtos.UserDTO;
import com.web.messanger.repos.UserRepository;
import java.util.ArrayList;
import java.util.HashSet;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final BCryptPasswordEncoder encoder;

  public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
    this.userRepository = userRepository;
    this.encoder = bCryptPasswordEncoder;
  }

  public void saveUser(UserDTO userdto) {
    User user = new User();

    user.setFirstname(userdto.firstname());
    user.setLastname(userdto.lastname());
    user.setUsername(userdto.username());
    user.setHashed_password(encoder.encode(userdto.password()));
    user.setBirthDate(userdto.birthDate());
    user.setMessages(new ArrayList<>());
    user.setGroups(new HashSet<>());
    userRepository.save(user);
  }
}
