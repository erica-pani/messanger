package com.web.messanger.controller;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import com.web.messanger.model.ChatMessage;

@Controller
public class ChatController {
    

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage message) {
        message.setTime(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        return message;
    }
    
    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage message, SimpMessageHeaderAccessor accessor) {
        message.setTime(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        accessor.getSessionAttributes().put("username", message.getSender());
        return message;
    }
}