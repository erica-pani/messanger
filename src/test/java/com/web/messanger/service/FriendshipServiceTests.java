package com.web.messanger.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.web.messanger.model.Friendship;
import com.web.messanger.model.FriendshipRequest;
import com.web.messanger.model.RequestStatus;
import com.web.messanger.model.User;
import com.web.messanger.repos.FriendshipRepository;
import com.web.messanger.repos.FriendshipRequestRepository;
import com.web.messanger.repos.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class FriendshipServiceTests {

  @Mock private UserRepository userRepository;

  @Mock private FriendshipRepository friendshipRepository;

  @Mock private FriendshipRequestRepository friendshipRequestRepository;

  @InjectMocks private FriendshipService target;

  User sender;
  User receiver;
  String validUsernameS = "sender";
  String validUsernameR = "empfänger";
  Long validUserIdS = 15L;
  Long validUserIdR = 17L;

  @BeforeEach
  public void setUpTestCase() {
    sender = new User();
    sender.setId(validUserIdS);
    sender.setUsername(validUsernameS);
    receiver = new User();
    receiver.setId(validUserIdR);
    receiver.setUsername(validUsernameR);
  }

  @AfterEach
  public void tearDownTestCase() {
    sender = null;
    receiver = null;
  }

  @Test
  public void getReceivedRequestsTest() {
    Long validRequestId = 3454L;
    FriendshipRequest friendshipRequest = new FriendshipRequest();
    friendshipRequest.setId(validRequestId);
    friendshipRequest.setReceiver(receiver);
    friendshipRequest.setSender(sender);

    when(userRepository.existsById(validUserIdR)).thenReturn(true);
    when(friendshipRequestRepository.findAllByReceiverId(validUserIdR))
        .thenReturn(List.of(friendshipRequest));

    List<FriendshipRequest> requests = target.getReceivedRequests(validUserIdR);

    assertTrue(requests.contains(friendshipRequest));
  }

  @Test
  public void getReceivedRequests_whenReceiverDoesNotExist() {

    when(userRepository.existsById(validUserIdR)).thenReturn(false);

    assertThrows(
        EntityNotFoundException.class,
        () -> {
          target.getReceivedRequests(validUserIdR);
        });
  }

  @Test
  public void getFriendsTest() {
    Long validFriendshipId = 3454L;
    Friendship friendship = new Friendship();
    friendship.setId(validFriendshipId);
    friendship.setUser1(receiver);
    friendship.setUser2(sender);

    when(userRepository.findById(validUserIdS)).thenReturn(Optional.of(sender));
    when(friendshipRepository.findByUser1OrUser2(sender, sender)).thenReturn(List.of(friendship));

    List<Friendship> friendships = target.getFriends(validUserIdS);

    assertTrue(friendships.contains(friendship));
  }

  @Test
  public void getFriends_whenUserNotFound() {

    when(userRepository.findById(validUserIdS)).thenReturn(Optional.empty());

    assertThrows(
        EntityNotFoundException.class,
        () -> {
          target.getFriends(validUserIdS);
        });
  }

  @Test
  public void sendFriendshipRequestTest() {
    when(userRepository.findById(validUserIdS)).thenReturn(Optional.of(sender));
    when(userRepository.findById(validUserIdR)).thenReturn(Optional.of(receiver));
    when(friendshipRequestRepository.save(any(FriendshipRequest.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    target.sendFriendshipRequest(validUserIdS, validUserIdR);

    ArgumentCaptor<FriendshipRequest> captor = ArgumentCaptor.forClass(FriendshipRequest.class);
    verify(friendshipRequestRepository).save(captor.capture());
    FriendshipRequest friendshipRequest = captor.getValue();

    assertEquals(sender, friendshipRequest.getSender());
    assertEquals(receiver, friendshipRequest.getReceiver());
    assertEquals(RequestStatus.PENDING, friendshipRequest.getRequestStatus());
  }

  @Test
  public void sendFriendshipRequest_whenSenderEqualsReceiver() {

    assertThrows(
        IllegalArgumentException.class,
        () -> {
          target.sendFriendshipRequest(validUserIdS, validUserIdS);
        });

    verify(friendshipRequestRepository, never()).save(any());
  }

  @Test
  public void sendFriendshipRequest_whenRequestAlreadyExists() {

    when(friendshipRequestRepository.existsBetweenUsers(validUserIdS, validUserIdR))
        .thenReturn(true);

    assertThrows(
        IllegalStateException.class,
        () -> {
          target.sendFriendshipRequest(validUserIdS, validUserIdR);
        });

    verify(friendshipRequestRepository, never()).save(any());
  }

  @Test
  public void sendFriendshipRequest_whenSenderNotFound() {

    when(friendshipRequestRepository.existsBetweenUsers(validUserIdS, validUserIdR))
        .thenReturn(false);
    when(userRepository.findById(validUserIdS)).thenReturn(Optional.empty());

    assertThrows(
        EntityNotFoundException.class,
        () -> {
          target.sendFriendshipRequest(validUserIdS, validUserIdR);
        });

    verify(friendshipRequestRepository, never()).save(any());
  }

  @Test
  public void sendFriendshipRequest_whenReceiverNotFound() {
    when(friendshipRequestRepository.existsBetweenUsers(validUserIdS, validUserIdR))
        .thenReturn(false);
    when(userRepository.findById(validUserIdS)).thenReturn(Optional.of(sender));
    when(userRepository.findById(validUserIdR)).thenReturn(Optional.empty());

    assertThrows(
        EntityNotFoundException.class,
        () -> {
          target.sendFriendshipRequest(validUserIdS, validUserIdR);
        });

    verify(friendshipRequestRepository, never()).save(any());
  }

  @Test
  public void replyToFriendshipRequestTest_whenAccepted() {
    Long validRequestId = 42092L;
    FriendshipRequest friendshipRequest = new FriendshipRequest();
    friendshipRequest.setId(validRequestId);
    friendshipRequest.setSender(sender);
    friendshipRequest.setReceiver(receiver);

    when(friendshipRequestRepository.findById(validRequestId))
        .thenReturn(Optional.of(friendshipRequest));
    when(friendshipRequestRepository.save(friendshipRequest))
        .thenAnswer(invocation -> invocation.getArgument(0));
    when(friendshipRepository.existsByUser1AndUser2(any(), any())).thenReturn(false);

    target.replyToFriendshipRequest(validRequestId, true);

    ArgumentCaptor<FriendshipRequest> savedFriendshipRequest =
        ArgumentCaptor.forClass(FriendshipRequest.class);
    verify(friendshipRequestRepository).save(savedFriendshipRequest.capture());
    assertEquals(RequestStatus.ACCEPTED, savedFriendshipRequest.getValue().getRequestStatus());

    ArgumentCaptor<Friendship> savedFriendship = ArgumentCaptor.forClass(Friendship.class);
    verify(friendshipRepository).save(savedFriendship.capture());
    assertTrue(
        sender.equals(savedFriendship.getValue().getUser1())
            || sender.equals(savedFriendship.getValue().getUser2()));
    assertTrue(
        receiver.equals(savedFriendship.getValue().getUser1())
            || receiver.equals(savedFriendship.getValue().getUser2()));
  }

  @Test
  public void replyToFriendshipRequest_whenRequestDoesNotExist() {}

  @Test
  public void replyToFriendshipRequest_whenAccepted() {}

  @Test
  public void replyToFriendshipRequestTest_whenDeclined() {
    Long validRequestId = 42092L;
    FriendshipRequest friendshipRequest = new FriendshipRequest();
    friendshipRequest.setId(validRequestId);
    friendshipRequest.setSender(sender);
    friendshipRequest.setReceiver(receiver);

    when(friendshipRequestRepository.findById(validRequestId))
        .thenReturn(Optional.of(friendshipRequest));

    target.replyToFriendshipRequest(validRequestId, false);

    assertEquals(RequestStatus.DECLINED, friendshipRequest.getRequestStatus());

    verify(friendshipRepository, never()).save(any());
  }
}
