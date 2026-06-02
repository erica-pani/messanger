package com.web.messanger.controller;

import com.web.messanger.model.Friendship;
import com.web.messanger.model.FriendshipRequest;
import com.web.messanger.service.FriendshipService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
            @RequestParam Long sender,
            @RequestParam Long receiver) {

        try {
            return ResponseEntity.status(201).body(friendshipService.sendFriendshipRequest(sender, receiver));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        }
    }

    @PostMapping("/request/reply")
    public ResponseEntity<?> replyToFriendshipRequest(
            @RequestParam Long id,
            @RequestParam Boolean reply) {

        try {
            return ResponseEntity.ok(friendshipService.replyToFriendshipRequest(id, reply));
        } catch (IllegalStateException | EntityNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
