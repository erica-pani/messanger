package com.web.messanger.config;

import com.web.messanger.service.CustomUserDetailsService;
import com.web.messanger.service.GroupService;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@AllArgsConstructor
public class WebSocketSecurityConfig implements WebSocketMessageBrokerConfigurer {

  private final GroupService groupService;
  private final CustomUserDetailsService userDetailsService;

  @Override
  public void configureClientInboundChannel(ChannelRegistration registration) {
    registration.interceptors(
        new ChannelInterceptor() {

          @SuppressWarnings("null")
          @Override
          public Message<?> preSend(Message<?> message, MessageChannel channel) {
            StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
            Map<String, Object> sessionAttributes = accessor.getSessionAttributes();

            if (SimpMessageType.CONNECT.equals(accessor.getMessageType())) {
              List<String> authHeaders = accessor.getNativeHeader("Authorization");
              if (authHeaders != null && !authHeaders.isEmpty()) {
                String authHeader = authHeaders.get(0);
                if (authHeader != null && authHeader.startsWith("Basic ")) {
                  try {
                    String base64Credentials = authHeader.substring("Basic ".length());
                    byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
                    String credentials = new String(credDecoded, StandardCharsets.UTF_8);
                    final String[] values = credentials.split(":", 2);
                    String username = values[0];

                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    if (userDetails != null && sessionAttributes != null) {
                      sessionAttributes.put("username", userDetails.getUsername());
                    }
                  } catch (Exception e) {
                    System.err.println(
                        "Fehler beim Parsen der Basic Auth im WS Connect: " + e.getMessage());
                  }
                }
              }
            }

            if (SimpMessageType.SUBSCRIBE.equals(accessor.getMessageType())) {
              String destination = accessor.getDestination();

              String username = (String) (sessionAttributes.get("username"));

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
