package com.web.messanger.controller;

import com.web.messanger.model.Friendship;
import com.web.messanger.model.FriendshipRequest;
import com.web.messanger.model.RequestStatus;
import com.web.messanger.model.User;
import com.web.messanger.repos.FriendshipRepository;
import com.web.messanger.repos.FriendshipRequestRepository;
import com.web.messanger.repos.UserRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/friendship")
public class FriendshipController {

  private final UserRepository userRepository;

  private final FriendshipRequestRepository friendshipRequestRepository;

  private final FriendshipRepository friendshipRepository;

  public FriendshipController(
      UserRepository userRepository,
      FriendshipRequestRepository friendshipRequestRepository,
      FriendshipRepository friendshipRepository) {
    this.userRepository = userRepository;
    this.friendshipRequestRepository = friendshipRequestRepository;
    this.friendshipRepository = friendshipRepository;
  }

  @GetMapping("/requests")
  public ResponseEntity<?> receivedFriendshipRequests(@RequestParam Long id) {

    if (!userRepository.existsById(id)) {
      return ResponseEntity.badRequest().body("User does not exist");
    }

    List<FriendshipRequest> requests =
        friendshipRequestRepository.findAllByReceiverId(id).stream()
            .filter(request -> request.getRequestStatus() == RequestStatus.PENDING)
            .toList();

    return ResponseEntity.ok(requests);
  }

  @GetMapping("/friends")
  public ResponseEntity<?> getFriends(@RequestParam Long id) {

    var user = userRepository.findById(id);

    if (user.isEmpty()) {
      return ResponseEntity.badRequest().body("User with this Id doesnt exist");
    }

    List<Friendship> friendships = friendshipRepository.findByUser1OrUser2(user.get(), user.get());

    System.out.println(friendships);

    return ResponseEntity.ok(friendships);
  }

  @PostMapping("/request/to")
  public ResponseEntity<?> sendFriendshipRequest(
      @RequestParam Long sender, @RequestParam Long receiver) {

    if (sender.equals(receiver)) {
      ResponseEntity.badRequest().body("Sender cannot be receiver");
    }

    if (friendshipRequestRepository.existsBetweenUsers(sender, receiver)) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body("Friendship request already sent");
    }

    Optional<User> userReceiver = userRepository.findById(receiver);
    Optional<User> userSender = userRepository.findById(sender);

    if (userSender.isEmpty() || userReceiver.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sender or receiver not found");
    }

    var request =
        FriendshipRequest.builder().sender(userSender.get()).receiver(userReceiver.get()).build();

    friendshipRequestRepository.save(request);

    return ResponseEntity.status(HttpStatus.CREATED).body(request);
  }

  /// reply = true ist accept und reply = false ist declined
  @PostMapping("/request/reply")
  public ResponseEntity<?> replyToFriendshipRequest(
      @RequestParam Long id, @RequestParam Boolean reply) {

    var request = friendshipRequestRepository.findById(id).orElse(null);

    if (request == null) {
      return ResponseEntity.badRequest().body("Request doesnt exist");
    }

    if (request.getRequestStatus() != RequestStatus.PENDING) {
      return ResponseEntity.badRequest().body("This request is already processed");
    }

    var partner = userRepository.findById(request.getSender().getId());
    var otherPartner = userRepository.findById(request.getReceiver().getId());

    if (partner.isEmpty() || otherPartner.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sender or receiver not found");
    }

    if (reply) {
      request.setRequestStatus(RequestStatus.ACCEPTED);
      friendshipRequestRepository.save(request);

      boolean exists =
          friendshipRepository.existsByUser1AndUser2(partner.get(), otherPartner.get())
              || friendshipRepository.existsByUser1AndUser2(otherPartner.get(), partner.get());

      if (!exists) {
        var friendship =
            Friendship.builder().user1(partner.get()).user2(otherPartner.get()).build();

        friendshipRepository.save(friendship);
      }

      return ResponseEntity.status(HttpStatus.ACCEPTED).body("Friendship request accepted");
    } else {
      request.setRequestStatus(RequestStatus.DECLINED);
      friendshipRequestRepository.save(request);

      return ResponseEntity.ok("Frienship request declined");
    }
  }
}
