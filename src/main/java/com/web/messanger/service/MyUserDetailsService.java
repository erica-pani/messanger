package com.web.messanger.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.web.messanger.model.User;
import com.web.messanger.model.UserPrincipal;
import com.web.messanger.repos.UserRepository;

@Service
public class MyUserDetailsService implements UserDetailsService{

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("Username found");

        User user = userRepository.findByUsername(username);

        if (user == null) {
            System.out.println("Username not found");
            throw new UsernameNotFoundException("Username not found"); 
        }

        System.out.println("Username found");

        return new UserPrincipal(user);
    }

    
}
