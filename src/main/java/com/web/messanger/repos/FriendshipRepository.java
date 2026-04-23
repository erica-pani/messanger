package com.web.messanger.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.web.messanger.model.Friendship;
import com.web.messanger.model.User;

public interface FriendshipRepository extends JpaRepository<Friendship, Long>{
    
    boolean existsByUser1AndUser2(Long user1, Long user2);

    List<Friendship> findByUser1OrUser2(User user1, User user2);
}
