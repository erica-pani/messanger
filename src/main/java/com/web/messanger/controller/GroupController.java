package com.web.messanger.controller;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.web.messanger.model.ChatMessage;
import com.web.messanger.model.Group;
import com.web.messanger.model.User;
import com.web.messanger.repos.GroupRepository;
import com.web.messanger.repos.UserRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/groups")
public class GroupController {

    private final GroupRepository groupRepository;

    private final UserRepository userRepository;
    
    public GroupController(GroupRepository groupRepository, UserRepository userRepository) {
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
    }

    @GetMapping
    public Collection<Group> getRelevantGroups(@RequestParam("username") String username) {
        Optional<User> user = Optional.ofNullable(userRepository.findByUsername(username));

        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        return user.get().getGroups();
    }

    @GetMapping("/{groupName}")
    public List<ChatMessage> loadGroupChat(@PathVariable String groupName) {
        
        Optional<Group> group = Optional.ofNullable(groupRepository.findByName(groupName));

        if (group.isPresent()) {
            return group.get().getMessages();
        }

        throw new EntityNotFoundException("Group not found");
    }

    @PostMapping("/create")
    public void createNewGroup(@RequestBody Group group) {
    
        groupRepository.save(group);
    }


    @PutMapping("/{groupName}/addGroupMember")
    public Group addGroupMember(@RequestParam Long id, @PathVariable String groupName, @RequestParam String username) {

        Optional<Group> group = groupRepository.findById(id);

        Optional<User> user = Optional.ofNullable(userRepository.findByUsername(username));

        if (user.isPresent() && (group.isPresent() && group.get().getName().equals(groupName))) {
            group.get().addUser(user.get());
            groupRepository.save(group.get());
            return group.get();
        }

        throw new EntityNotFoundException("Group does not exist");

    }

    @DeleteMapping("/{groupName}/removeGroupMember")
    public Group removeGroupMembers(@RequestParam Long id, @PathVariable String groupName, @RequestParam String username) {
        
        Optional<Group> group = groupRepository.findById(id);

        Optional<User> user = Optional.ofNullable(userRepository.findByUsername(username));

        if (user.isPresent() && (group.isPresent() && group.get().getName().equals(groupName))) {
            group.get().removeUser(user.get());
            groupRepository.save(group.get());
            return group.get();
        }

        throw new EntityNotFoundException("Group does not exist");
    }

    @DeleteMapping("/{groupName}/deleteGroup")
    public ResponseEntity<Group> deleteGroup(@RequestParam Long id, @PathVariable String groupName) {

        Optional<Group> group = groupRepository.findById(id);

        if (group.isPresent() && group.get().getName().equals(groupName)) {
            groupRepository.delete(group.get());
            return ResponseEntity.ok(group.get());
        }

        throw new EntityNotFoundException();
    }
}
