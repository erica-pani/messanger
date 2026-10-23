package com.web.messanger.service;

import com.web.messanger.model.ChatMessage;
import com.web.messanger.model.Group;
import com.web.messanger.model.User;
import com.web.messanger.model.dtos.GroupDTO;
import com.web.messanger.repos.GroupRepository;
import com.web.messanger.repos.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class GroupService {

  private final GroupRepository groupRepository;
  private final UserRepository userRepository;

  public GroupService(GroupRepository groupRepository, UserRepository userRepository) {
    this.groupRepository = groupRepository;
    this.userRepository = userRepository;
  }

  public Collection<Group> getRelevantGroups(Long userId) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userId));

    return user.getGroups();
  }

  public List<ChatMessage> loadGroupChat(Long groupId) {
    Group group =
        groupRepository
            .findById(groupId)
            .orElseThrow(() -> new EntityNotFoundException("Group not found"));

    List<ChatMessage> messages = group.getMessages();

    for (ChatMessage message : messages) {
      if (message.getSender() == null) {
        message.setSenderName("Unbekannt");
      } else {
        message.setSenderName(message.getSender().getUsername());
      }
    }

    return messages;
  }

  public Group createNewGroup(GroupDTO groupdto) {
    Set<User> members = new HashSet<>();

    for (Long id : groupdto.userIds()) {
      User user =
          userRepository
              .findById(id)
              .orElseThrow(() -> new EntityNotFoundException("User does not exist: " + id));
      members.add(user);
    }

    Group group = new Group();
    group.setName(groupdto.name());
    group.setMessages(new ArrayList<>());
    group.setUsers(members);

    return groupRepository.save(group);
  }

  public Group addGroupMember(Long id, String groupName, String username) {
    Group group =
        groupRepository
            .findById(id)
            .filter(g -> g.getName().equals(groupName))
            .orElseThrow(() -> new EntityNotFoundException("Group does not exist"));

    User user =
        Optional.ofNullable(userRepository.findByUsername(username))
            .orElseThrow(() -> new EntityNotFoundException("User does not exist"));

    group.addUser(user);
    return groupRepository.save(group);
  }

  public Group removeGroupMember(Long id, String groupName, String username) {
    Group group =
        groupRepository
            .findById(id)
            .filter(g -> g.getName().equals(groupName))
            .orElseThrow(() -> new EntityNotFoundException("Group does not exist"));

    User user =
        Optional.ofNullable(userRepository.findByUsername(username))
            .orElseThrow(() -> new EntityNotFoundException("User does not exist"));

    group.removeUser(user);
    return groupRepository.save(group);
  }

  public Group deleteGroup(Long id, String groupName) {
    Group group =
        groupRepository
            .findById(id)
            .filter(g -> g.getName().equals(groupName))
            .orElseThrow(EntityNotFoundException::new);

    groupRepository.delete(group);
    return group;
  }

  public boolean isUserinGroup(String username, String groupId) {
    // TODO Auto-generated method stub
    return true;
  }
}
