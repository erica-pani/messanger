package com.web.messanger.controller;

import java.util.Collection;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.web.messanger.model.Group;
import com.web.messanger.model.User;
import com.web.messanger.repos.GroupRepository;
import com.web.messanger.repos.UserRepository;

@RestController
@RequestMapping("/groups")
public class GroupController {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;
    

    @GetMapping("/")
    public Collection<Group> getRelevantGroups(@RequestParam String username) {
        Optional<User> user = Optional.ofNullable(userRepository.findByUsername(username));

        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        return user.get().getGroups();
    }
    
    @GetMapping("path")
    public void getGroupMembers() {
        
    }

    public void createNewGroup() {

    }

    public void addNewGroupMembers() {

    }

    public void deleteGroupMembers() {

    }

    public void deleteGroup() {

    }

    public void changeGroupParameters() {

    }
}
