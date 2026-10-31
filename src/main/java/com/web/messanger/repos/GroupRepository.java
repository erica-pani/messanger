package com.web.messanger.repos;

import com.web.messanger.model.Group;
import com.web.messanger.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GroupRepository extends JpaRepository<Group, Long> {

  Group findByName(String name);

  @Query(
      """
      SELECT CASE WHEN COUNT(g) > 0 THEN true ELSE false END FROM Group g
      JOIN g.users u1
      JOIN g.users u2
      WHERE g.isPrivate = true
      AND((u1 = :user1 AND u2 = :user2) OR (u1 = :user2 AND u2 = :user1))
      AND SIZE(g.users) = 2
    """)
  boolean existsAsPrivateChat(@Param("user1") User user1, @Param("user2") User user2);
}
