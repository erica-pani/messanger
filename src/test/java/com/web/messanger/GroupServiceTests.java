package com.web.messanger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.web.messanger.model.ChatMessage;
import com.web.messanger.model.Group;
import com.web.messanger.model.GroupDTO;
import com.web.messanger.model.User;
import com.web.messanger.repos.GroupRepository;
import com.web.messanger.repos.UserRepository;
import com.web.messanger.service.GroupService;
import jakarta.persistence.EntityNotFoundException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
public class GroupServiceTests {

  @Mock private UserRepository userRepository;

  @Mock private GroupRepository groupRepository;

  @InjectMocks private GroupService target;

  GroupDTO groupDTO;
  Group group;
  User user;
  String validGroupName = "gruppe";
  String username = "username";
  Long validGroupId = 17L;
  Long validUserId = 35628L;

  @BeforeEach
  public void setUpTestCase() {
    groupDTO = new GroupDTO();
    group = new Group();
    group.setName(validGroupName);
    group.setId(validGroupId);
    user = new User();
    user.setUsername(username);
    user.setId(validUserId);
  }

  @AfterEach
  public void tearDowntestCase() {
    groupDTO = null;
    group = null;
    user = null;
  }

  @Test
  public void getRelevantGroupsTest() {
    user.setGroups(Set.of(group));

    when(userRepository.findByUsername(username)).thenReturn(user);

    Collection<Group> userGroups = target.getRelevantGroups(username);

    assertEquals(1, userGroups.size());
    assertTrue(userGroups.contains(group));
  }

  @Test
  public void getRelevantGroups_whenUserNotFound() {
    user.setGroups(Set.of(group));

    when(userRepository.findByUsername(username)).thenReturn(null);

    assertThrows(
        UsernameNotFoundException.class,
        () -> {
          target.getRelevantGroups(username);
        });
  }

  @Test
  public void loadGroupChatTest() {

    ChatMessage message1 = new ChatMessage();
    ChatMessage message2 = new ChatMessage();

    List<ChatMessage> messages = List.of(message1, message2);
    group.setMessages(messages);

    when(groupRepository.findByName(validGroupName)).thenReturn(group);

    List<ChatMessage> result = target.loadGroupChat(validGroupName);

    assertEquals(2, result.size());
    assertTrue(result.contains(message1));
    assertTrue(result.contains(message2));
  }

  @Test
  public void loadGroupChat_whenGroupNotFound() {
    ChatMessage message1 = new ChatMessage();
    ChatMessage message2 = new ChatMessage();

    List<ChatMessage> messages = List.of(message1, message2);
    group.setMessages(messages);

    when(groupRepository.findByName(validGroupName)).thenReturn(null);

    assertThrows(
        EntityNotFoundException.class,
        () -> {
          target.loadGroupChat(validGroupName);
        });
  }

  @Test
  public void createNewGroupTest() {

    String validGroupName = "neue Gruppe";
    Set<Long> userIds = Set.of(Long.valueOf(15), Long.valueOf(17));
    groupDTO.setName(validGroupName);
    groupDTO.setUserIds(userIds);

    User user1 = new User();
    User user2 = new User();
    user1.setId((long) 15);
    user2.setId((long) 17);

    when(groupRepository.save(any(Group.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    when(userRepository.findById((long) 15)).thenReturn(Optional.of(user1));
    when(userRepository.findById((long) 17)).thenReturn(Optional.of(user2));

    Group result = target.createNewGroup(groupDTO);

    assertEquals(validGroupName, result.getName());
    assertTrue(result.getUsers().size() == 2);

    ArgumentCaptor<Group> captor = ArgumentCaptor.forClass(Group.class);
    Mockito.verify(groupRepository).save(captor.capture());

    Group savedGroup = captor.getValue();
    assertEquals(validGroupName, savedGroup.getName());
    assertEquals(2, savedGroup.getUsers().size());
  }

  @Test
  public void addGroupMemberTest() {}

  @Test
  public void removeGroupMemberTest() {}

  @Test
  public void deleteGroupTest() {

    when(groupRepository.findById(validGroupId)).thenReturn(Optional.of(group));

    Group result = target.deleteGroup(validGroupId, validGroupName);

    assertEquals(group, result);
    verify(groupRepository).delete(result);
  }

  @Test
  public void deleteGroup_whenNameDoesNotMatch() {

    when(groupRepository.findById(validGroupId)).thenReturn(Optional.of(group));

    assertThrows(
        EntityNotFoundException.class,
        () -> {
          target.deleteGroup(validGroupId, "GruppeDieEsNichtGibt");
        });

    verify(groupRepository, never()).delete(any());
  }

  @Test
  public void deleteGroup_whenNotFound() {

    when(groupRepository.findById(validGroupId)).thenReturn(Optional.empty());

    assertThrows(
        EntityNotFoundException.class,
        () -> {
          target.deleteGroup(validGroupId, validGroupName);
        });

    verify(groupRepository, never()).delete(any());
  }
}
