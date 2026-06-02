package com.web.messanger.service;

import com.web.messanger.model.ChatMessage;
import com.web.messanger.model.Group;
import com.web.messanger.model.GroupDTO;
import com.web.messanger.model.User;
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

  public Collection<Group> getRelevantGroups(String username) {
    User user =
        Optional.ofNullable(userRepository.findByUsername(username))
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

    return user.getGroups();
  }

  public List<ChatMessage> loadGroupChat(String groupName) {
    Group group =
        Optional.ofNullable(groupRepository.findByName(groupName))
            .orElseThrow(() -> new EntityNotFoundException("Group not found"));

    return group.getMessages();
  }

  public Group createNewGroup(GroupDTO groupdto) {
    Set<User> members = new HashSet<>();

    for (Long id : groupdto.getUserIds()) {
      User user =
          userRepository
              .findById(id)
              .orElseThrow(() -> new EntityNotFoundException("User does not exist: " + id));
      members.add(user);
    }

    Group group = new Group();
    group.setName(groupdto.getName());
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
}
