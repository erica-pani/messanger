package com.web.messanger.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.web.messanger.model.Friendship;

public interface FriendshipRepository extends JpaRepository<Friendship, Long>{
    
    @Query("""
        SELECT COUNT(fr) > 0
        FROM Frienship fr
        WHERE (fr.user1.id = :user1 AND fr.user2.id = :user2)       
    """)
    boolean existsByUser1AndUser2(Long user1, Long user2);
}
