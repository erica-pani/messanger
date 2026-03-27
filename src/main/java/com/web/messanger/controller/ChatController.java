package com.web.messanger.controller;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;

import com.web.messanger.model.ChatMessage;
import com.web.messanger.model.User;
import com.web.messanger.repos.MessageRepository;
import com.web.messanger.repos.UserRepository;

@Controller
public class ChatController {

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private SimpMessageSendingOperations messageTemplate;

        private MessageRepository messageRepository;
    

    @MessageMapping("/chat.sendMessage")
    public ChatMessage sendMessage(@Payload ChatMessage message, SimpMessageHeaderAccessor accessor) {

        String username = (String) accessor.getSessionAttributes().get("username");

        Optional<User> sender = Optional.ofNullable(userRepository.findByUsername(username));

        if (sender.isPresent()) {
            message.setSender(sender.get());
        } else {
            throw new UsernameNotFoundException("User not found");
        }
        
        message.setTime(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));

        messageTemplate.convertAndSend("/topic/public/" + message.getGroup().getName(), message);

        messageRepository.save(message);

        return message;
    }
    
    @MessageMapping("/chat.addUser")
    public ChatMessage addUser(@Payload ChatMessage message, SimpMessageHeaderAccessor accessor) {

        message.setTime(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        accessor.getSessionAttributes().put("username", message.getSenderName());

        messageTemplate.convertAndSend("/topic/public/" + message.getGroup().getName(), message);

        return message;
    }


    @Autowired
    public void setRepository(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }
}