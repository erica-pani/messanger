package com.web.messanger.controller;

import com.web.messanger.model.Friendship;
import com.web.messanger.model.FriendshipRequest;
import com.web.messanger.service.FriendshipService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/friendship")
public class FriendshipController {

  private final FriendshipService friendshipService;

  public FriendshipController(FriendshipService friendshipService) {
    this.friendshipService = friendshipService;
  }

  @GetMapping("/requests")
  public ResponseEntity<List<FriendshipRequest>> receivedFriendshipRequests(@RequestParam Long id) {
    return ResponseEntity.ok(friendshipService.getReceivedRequests(id));
  }

  @GetMapping("/friends")
  public ResponseEntity<List<Friendship>> getFriends(@RequestParam Long id) {
    return ResponseEntity.ok(friendshipService.getFriends(id));
  }

  @PostMapping("/request/to")
  public ResponseEntity<?> sendFriendshipRequest(
      @RequestParam Long sender, @RequestParam Long receiver) {
    return new ResponseEntity<>(
        friendshipService.sendFriendshipRequest(sender, receiver), HttpStatus.CREATED);
  }

  @PostMapping("/request/reply")
  public ResponseEntity<?> replyToFriendshipRequest(
      @RequestParam Long id, @RequestParam Boolean reply) {
    return ResponseEntity.ok(friendshipService.replyToFriendshipRequest(id, reply));
  }
}
