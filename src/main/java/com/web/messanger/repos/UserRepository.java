package com.web.messanger.repos;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.web.messanger.model.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long>{

    Optional<User> findById(Long id);
    User findByUsername(String usernmae);
} 