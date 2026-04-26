package com.web.messanger.repos;

import com.web.messanger.model.FriendshipRequest;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FriendshipRequestRepository extends JpaRepository<FriendshipRequest, Long> {

  @Query(
      """
        SELECT COUNT(fr) > 0
        FROM FriendshipRequest fr
        WHERE (fr.sender.id = :sender AND fr.receiver.id = :receiver)
        OR (fr.sender.id = :receiver AND fr.receiver.id = :sender)
    """)
  boolean existsBetweenUsers(Long sender, Long receiver);

  List<FriendshipRequest> findAllByReceiverId(Long user);
}
