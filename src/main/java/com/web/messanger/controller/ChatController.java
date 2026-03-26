package com.web.messanger.controller;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;

import com.web.messanger.model.ChatMessage;
import com.web.messanger.model.User;
import com.web.messanger.repos.UserRepository;

@Controller
public class ChatController {

    @Autowired
    private UserRepository userRepository;
    

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage message, SimpMessageHeaderAccessor accessor) {

        String username = (String) accessor.getSessionAttributes().get("username");

        Optional<User> sender = Optional.ofNullable(userRepository.findByUsername(username));

        if (sender.isPresent()) {
            message.setSender(sender.get());
        } else {
            throw new UsernameNotFoundException("User not found");
        }
        
        message.setTime(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));

        return message;
    }
    
    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage message, SimpMessageHeaderAccessor accessor) {

        message.setTime(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        accessor.getSessionAttributes().put("username", message.getSenderName());
        return message;
    }
}