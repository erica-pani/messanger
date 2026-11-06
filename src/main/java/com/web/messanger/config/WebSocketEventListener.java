package com.web.messanger.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class WebSocketEventListener {

  // private final SimpMessageSendingOperations messageTemplate;

  /*@EventListener
  public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
    StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
    String username = (String) headerAccessor.getSessionAttributes().get("username");
    if (username != null) {
      log.info("User disconnected: {}", username);
      var message =
          ChatMessage.builder()
              .content(username + " left the chat")
              .senderName(username)
              .time(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")))
              .build();
      messageTemplate.convertAndSend("/topic/public/" + message.getGroup().getId(), message);
    }
  }*/
}
