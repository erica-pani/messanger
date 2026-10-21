package com.web.messanger.repos;

import com.web.messanger.model.User;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {

  Optional<User> findById(Long id);

  User findByUsername(String usernmae);
}
