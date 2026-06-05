package com.web.messanger.service;

import com.web.messanger.model.User;
import com.web.messanger.model.UserPrincipal;
import com.web.messanger.repos.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  public MyUserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

    User user = userRepository.findByUsername(username);

    if (user == null) {
      throw new UsernameNotFoundException("Username not found");
    }

    return new UserPrincipal(user);
  }
}
