package com.web.messanger.controller;

import com.web.messanger.model.ChatMessage;
import com.web.messanger.model.Group;
import com.web.messanger.model.User;
import com.web.messanger.repos.GroupRepository;
import com.web.messanger.repos.MessageRepository;
import com.web.messanger.repos.UserRepository;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

  private final UserRepository userRepository;

  private final SimpMessageSendingOperations messageTemplate;

  private final MessageRepository messageRepository;

  private final GroupRepository groupRepository;

  public ChatController(
      UserRepository userRepository,
      SimpMessageSendingOperations messageTemplate,
      MessageRepository messageRepository,
      GroupRepository groupRepository) {
    this.userRepository = userRepository;
    this.messageTemplate = messageTemplate;
    this.messageRepository = messageRepository;
    this.groupRepository = groupRepository;
  }

  @MessageMapping("/chat.sendMessage")
  public ChatMessage sendMessage(@Payload ChatMessage message, SimpMessageHeaderAccessor accessor) {

    Long groupId = message.getGroupId();

    Optional<Group> group = groupRepository.findById(groupId);
    Optional<User> sender = userRepository.findByUsername(message.getSenderName());

    if (sender.isPresent() && group.isPresent()) {
      message.setSender(sender.get());
      message.setGroup(group.get());
    } else {
      throw new UsernameNotFoundException("User not found");
    }

    message.setTime(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));

    ChatMessage savedMessage = messageRepository.save(message);

    messageTemplate.convertAndSend(
        "/topic/public/" + savedMessage.getGroup().getId(), savedMessage);

    return savedMessage;
  }

  @MessageMapping("/chat.addUser")
  public ChatMessage addUser(@Payload ChatMessage message, SimpMessageHeaderAccessor accessor) {

    message.setTime(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
    accessor.getSessionAttributes().put("username", message.getSenderName());

    messageTemplate.convertAndSend("/topic/public/" + message.getGroup().getId(), message);

    return message;
  }
}
