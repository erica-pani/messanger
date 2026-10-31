package com.web.messanger.repos;

import com.web.messanger.model.ChatMessage;
import org.springframework.data.repository.CrudRepository;

public interface MessageRepository extends CrudRepository<ChatMessage, Long> {}
