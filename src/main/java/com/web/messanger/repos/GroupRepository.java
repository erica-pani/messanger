package com.web.messanger.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.web.messanger.model.Group;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long>{

    
} 
