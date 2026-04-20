package com.web.messanger.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.web.messanger.model.FriendshipRequest;
import com.web.messanger.model.User;
import com.web.messanger.repos.FriendshipRequestRepository;
import com.web.messanger.repos.UserRepository;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.HttpSecurityDslKt;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequestMapping("/friendship")
public class FriendshipController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FriendshipRequestRepository friendshipRequestRepository;
    
    @PutMapping("/request/to")
    public ResponseEntity<?> sendFriendshipRequest(@RequestParam Long sender, @RequestParam Long receiver) {

        if (sender.equals(receiver)) {
            ResponseEntity.badRequest().body("Sender cannot be receiver");
        }

        if (friendshipRequestRepository.existsBetweenUsers(sender, receiver)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("Friendship request already sent");
        }

        Optional<User> userReceiver = userRepository.findById(receiver); 
        Optional<User> userSender = userRepository.findById(sender);

        if (userSender.isEmpty() || userReceiver.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Sender or receiver not found");
        }

        var request = FriendshipRequest.builder()
                    .sender(userSender.get())
                    .receiver(userReceiver.get())
                    .build();

            friendshipRequestRepository.save(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(request);
    }
}
