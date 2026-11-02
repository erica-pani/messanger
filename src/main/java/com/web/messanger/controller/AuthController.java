package com.web.messanger.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.web.messanger.service.CustomUserDetailsService;

import lombok.AllArgsConstructor;

@RestController 
@AllArgsConstructor 
public class AuthController {

    public record LoginRequest(
        String username,
        String password
    ) {}

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {

        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.username());
        
        return ResponseEntity.ok("Token oder so");

    }
}
