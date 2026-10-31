package com.web.messanger.service;

import com.web.messanger.model.Friendship;
import com.web.messanger.model.FriendshipRequest;
import com.web.messanger.model.RequestStatus;
import com.web.messanger.model.User;
import com.web.messanger.repos.FriendshipRepository;
import com.web.messanger.repos.FriendshipRequestRepository;
import com.web.messanger.repos.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class FriendshipService {

  private final UserRepository userRepository;
  private final FriendshipRequestRepository friendshipRequestRepository;
  private final FriendshipRepository friendshipRepository;

  public FriendshipService(
      UserRepository userRepository,
      FriendshipRequestRepository friendshipRequestRepository,
      FriendshipRepository friendshipRepository) {
    this.userRepository = userRepository;
    this.friendshipRequestRepository = friendshipRequestRepository;
    this.friendshipRepository = friendshipRepository;
  }

  public List<FriendshipRequest> getReceivedRequests(Long id) {
    if (!userRepository.existsById(id)) {
      throw new EntityNotFoundException("User does not exist");
    }

    return friendshipRequestRepository.findAllByReceiverId(id).stream()
        .filter(req -> req.getRequestStatus() == RequestStatus.PENDING)
        .toList();
  }

  public List<Friendship> getFriends(Long id) {
    User user =
        userRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));

    return friendshipRepository.findByUser1OrUser2(user, user);
  }

  public FriendshipRequest sendFriendshipRequest(Long sender, Long receiver) {

    if (sender.equals(receiver)) {
      throw new IllegalArgumentException("Sender cannot be receiver");
    }

    if (friendshipRequestRepository.existsBetweenUsers(sender, receiver)) {
      throw new IllegalStateException("Friendship request already sent");
    }

    User senderUser =
        userRepository
            .findById(sender)
            .orElseThrow(() -> new EntityNotFoundException("Sender not found"));

    User receiverUser =
        userRepository
            .findById(receiver)
            .orElseThrow(() -> new EntityNotFoundException("Receiver not found"));

    FriendshipRequest request =
        FriendshipRequest.builder().sender(senderUser).receiver(receiverUser).build();

    return friendshipRequestRepository.save(request);
  }

  public String replyToFriendshipRequest(Long id, Boolean reply) {

    FriendshipRequest request =
        friendshipRequestRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Request not found"));

    if (request.getRequestStatus() != RequestStatus.PENDING) {
      throw new IllegalStateException("This request is already processed");
    }

    User sender = request.getSender();
    User receiver = request.getReceiver();

    if (reply) {
      request.setRequestStatus(RequestStatus.ACCEPTED);
      friendshipRequestRepository.save(request);

      boolean exists =
          friendshipRepository.existsByUser1AndUser2(sender, receiver)
              || friendshipRepository.existsByUser1AndUser2(receiver, sender);

      if (!exists) {
        Friendship friendship = Friendship.builder().user1(sender).user2(receiver).build();

        friendshipRepository.save(friendship);
      }

      return "Friendship request accepted";
    }

    request.setRequestStatus(RequestStatus.DECLINED);
    friendshipRequestRepository.save(request);

    return "Friendship request declined";
  }
}
