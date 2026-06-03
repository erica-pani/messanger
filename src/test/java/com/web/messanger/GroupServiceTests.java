package com.web.messanger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.web.messanger.model.Group;
import com.web.messanger.model.GroupDTO;
import com.web.messanger.model.User;
import com.web.messanger.repos.GroupRepository;
import com.web.messanger.repos.UserRepository;
import com.web.messanger.service.GroupService;
import jakarta.persistence.EntityNotFoundException;
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

@ExtendWith(MockitoExtension.class)
public class GroupServiceTests {

  @Mock private UserRepository userRepository;

  @Mock private GroupRepository groupRepository;

  @InjectMocks private GroupService target;

  GroupDTO groupDTO;
  Group group;
  String validName = "gruppe";
  Long validId = 17L;

  @BeforeEach
  public void setUpTestCase() {
    groupDTO = new GroupDTO();
    group = new Group();
    group.setName(validName);
    group.setId(validId);
  }

  @AfterEach
  public void tearDowntestCase() {
    groupDTO = null;
    group = null;
  }

  @Test
  public void getRelevantGroupsTest() {}

  @Test
  public void loadGroupChatTest() {}

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

    when(groupRepository.findById(validId)).thenReturn(Optional.of(group));

    Group result = target.deleteGroup(validId, validName);

    assertEquals(group, result);
    verify(groupRepository).delete(result);
  }

  @Test
  public void deleteGroup_whenNameDoesNotMatch() {

    when(groupRepository.findById(validId)).thenReturn(Optional.of(group));

    assertThrows(
        EntityNotFoundException.class,
        () -> {
          target.deleteGroup(validId, "GruppeDieEsNichtGibt");
        });

    verify(groupRepository, never()).delete(any());
  }

  @Test
  public void deleteGroup_whenNotFound() {

    when(groupRepository.findById(validId)).thenReturn(Optional.empty());

    assertThrows(
        EntityNotFoundException.class,
        () -> {
          target.deleteGroup(validId, validName);
        });

    verify(groupRepository, never()).delete(any());
  }
}
