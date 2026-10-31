package com.web.messanger.service;

import com.web.messanger.controller.mapper.GroupMapper;
import com.web.messanger.model.ChatMessage;
import com.web.messanger.model.Group;
import com.web.messanger.model.User;
import com.web.messanger.model.dtos.GroupDTO;
import com.web.messanger.model.dtos.GroupResponseDTO;
import com.web.messanger.repos.GroupRepository;
import com.web.messanger.repos.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GroupService {

  private final GroupRepository groupRepository;
  private final UserRepository userRepository;
  private final GroupMapper groupMapper;

  public GroupService(
      GroupRepository groupRepository, UserRepository userRepository, GroupMapper groupMapper) {
    this.groupRepository = groupRepository;
    this.userRepository = userRepository;
    this.groupMapper = groupMapper;
  }

  public Collection<GroupResponseDTO> getRelevantGroups(Long userId) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userId));

    Set<GroupResponseDTO> groups = new HashSet<>();
    for (Group group : new ArrayList<>(user.getGroups())) {
      if (group.isPrivate()) {
        String name =
            group.getUsers().stream()
                .filter(u -> !u.getUsername().equals(user.getUsername()))
                .findFirst()
                .get()
                .getUsername();
        group.setName(name);
      }
      groups.add(groupMapper.toGroupDTO(group));
    }
    return groups;
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

  public Group addGroupMember(Long id, String groupName, Long userId) {
    Group group =
        groupRepository
            .findById(id)
            .filter(g -> g.getName().equals(groupName))
            .orElseThrow(() -> new EntityNotFoundException("Group does not exist"));

    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User does not exist"));

    group.addUser(user);
    return groupRepository.save(group);
  }

  public Group removeGroupMember(Long id, String groupName, Long userId) {
    Group group =
        groupRepository
            .findById(id)
            .filter(g -> g.getName().equals(groupName))
            .orElseThrow(() -> new EntityNotFoundException("Group does not exist"));

    User user =
        userRepository
            .findById(userId)
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

    for (User user : new ArrayList<>(group.getUsers())) {
      group.removeUser(user);
    }
    groupRepository.delete(group);
    return group;
  }

  @Transactional(readOnly = true)
  public boolean isUserinGroup(String username, Long groupId) {
    Group group =
        groupRepository.findById(groupId).orElseThrow(() -> new EntityNotFoundException());

    return group.getUsers().stream().anyMatch(user -> user.getUsername().equals(username));
  }

  public Group createPrivateChat(Long userId1, Long userId2) {
    User user1 = userRepository.findById(userId1).orElseThrow(() -> new EntityNotFoundException());

    User user2 = userRepository.findById(userId2).orElseThrow(() -> new EntityNotFoundException());

    if (groupRepository.existsAsPrivateChat(user1, user2)) {
      throw new IllegalStateException();
    }

    Group group = new Group();
    group.setMessages(new ArrayList<>());
    group.setUsers(new HashSet<>());
    group.setPrivate(true);
    group.addUser(user1);
    group.addUser(user2);

    return groupRepository.save(group);
  }
}
