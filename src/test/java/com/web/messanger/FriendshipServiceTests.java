package com.web.messanger;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.web.messanger.model.Friendship;
import com.web.messanger.model.FriendshipRequest;
import com.web.messanger.model.User;
import com.web.messanger.repos.FriendshipRepository;
import com.web.messanger.repos.FriendshipRequestRepository;
import com.web.messanger.repos.UserRepository;
import com.web.messanger.service.FriendshipService;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

    assertThrows(EntityNotFoundException.class, () -> {
        target.getFriends(validUserIdS);
    });
  }
}
