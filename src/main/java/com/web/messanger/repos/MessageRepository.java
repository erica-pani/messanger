package com.web.messanger.repos;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.web.messanger.model.ChatMessage;

@Repository
public interface MessageRepository extends CrudRepository<ChatMessage, Long>{
    
}
