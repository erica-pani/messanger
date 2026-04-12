package com.web.messanger.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.web.messanger.model.Friendship;

public interface FriendshipRepository extends JpaRepository<Friendship, Long>{
    
}
