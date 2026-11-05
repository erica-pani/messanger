package com.web.messanger.model.dtos;

import com.web.messanger.model.ChatMessage;

public record GroupResponseDTO(
    Long id, String name, boolean isPrivate, ChatMessage lastSendMessage) {}
