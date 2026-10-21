package com.web.messanger.repos;

import com.web.messanger.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group, Long> {

  Group findByName(String name);
}
