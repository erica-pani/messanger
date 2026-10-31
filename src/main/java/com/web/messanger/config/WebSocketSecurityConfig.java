package com.web.messanger.config;

import com.web.messanger.service.GroupService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@AllArgsConstructor
public class WebSocketSecurityConfig implements WebSocketMessageBrokerConfigurer {

  private final GroupService groupService;

  @Override
  public void configureClientInboundChannel(ChannelRegistration registration) {
    registration.interceptors(
        new ChannelInterceptor() {

          @Override
          public Message<?> preSend(Message<?> message, MessageChannel channel) {
            StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

            if (SimpMessageType.SUBSCRIBE.equals(accessor.getMessageType())) {
              String destination = accessor.getDestination();
              String username = accessor.getUser() != null ? accessor.getUser().getName() : null;

              if (username == null) {
                throw new IllegalArgumentException("Nicht autorisiert: Kein Benutzer gefunden.");
              }

              String groupId = destination.split("/")[3];

              if (!groupService.isUserinGroup(username, Long.valueOf(groupId))) {
                throw new AccessDeniedException("Nicht autorisiert: ");
              }
            }

            return message;
          }
        });
  }
}
