package com.web.messanger.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.web.messanger.model.FriendshipRequest;

public interface FriendshipRequestRepository extends JpaRepository<FriendshipRequest, Long>{
    
}
