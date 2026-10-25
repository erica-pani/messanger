package com.web.messanger.controller;

import com.web.messanger.model.ChatMessage;
import com.web.messanger.model.Group;
import com.web.messanger.model.UserPrincipal;
import com.web.messanger.model.dtos.GroupDTO;
import com.web.messanger.service.GroupService;
import jakarta.validation.Valid;
import java.util.Collection;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/groups")
public class GroupController {

  private final GroupService groupService;

  public GroupController(GroupService groupService) {
    this.groupService = groupService;
  }

  @GetMapping("/")
  public Collection<Group> getRelevantGroups(@AuthenticationPrincipal UserDetails userDetails) {
    return groupService.getRelevantGroups(((UserPrincipal) userDetails).getId());
  }

  @GetMapping("/{groupId}")
  public ResponseEntity<List<ChatMessage>> loadGroupChat(@PathVariable Long groupId) {
    return new ResponseEntity<>(groupService.loadGroupChat(groupId), HttpStatus.OK);
  }

  @PostMapping("/create")
  public ResponseEntity<Group> createNewGroup(@Valid @RequestBody GroupDTO groupdto) {
    return ResponseEntity.ok(groupService.createNewGroup(groupdto));
  }

  @PutMapping("/{groupName}/member")
  public ResponseEntity<Group> addGroupMember(
      @RequestParam Long id, @PathVariable String groupName, @RequestParam Long userId) {

    return new ResponseEntity<>(groupService.addGroupMember(id, groupName, userId), HttpStatus.OK);
  }

  @DeleteMapping("/{groupName}/member")
  public ResponseEntity<Group> removeGroupMember(
      @RequestParam Long groupId, @PathVariable String groupName, @RequestParam Long userId) {

    return new ResponseEntity<>(
        groupService.removeGroupMember(groupId, groupName, userId), HttpStatus.OK);
  }

  @DeleteMapping("/{groupName}")
  public ResponseEntity<Group> deleteGroup(@RequestParam Long id, @PathVariable String groupName) {

    return ResponseEntity.ok(groupService.deleteGroup(id, groupName));
  }
}
