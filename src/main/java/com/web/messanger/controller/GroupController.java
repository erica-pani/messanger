package com.web.messanger.controller;

import com.web.messanger.model.ChatMessage;
import com.web.messanger.model.Group;
import com.web.messanger.model.GroupDTO;
import com.web.messanger.service.GroupService;
import java.util.Collection;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/groups")
public class GroupController {

  private final GroupService groupService;

  public GroupController(GroupService groupService) {
    this.groupService = groupService;
  }

  @GetMapping
  public Collection<Group> getRelevantGroups(@RequestParam("username") String username) {
    return groupService.getRelevantGroups(username);
  }

  @GetMapping("/{groupName}")
  public List<ChatMessage> loadGroupChat(@PathVariable String groupName) {
    return groupService.loadGroupChat(groupName);
  }

  @PostMapping("/create")
  public ResponseEntity<Group> createNewGroup(@RequestBody GroupDTO groupdto) {
    return ResponseEntity.ok(groupService.createNewGroup(groupdto));
  }

  @PutMapping("/{groupName}/addGroupMember")
  public Group addGroupMember(
      @RequestParam Long id, @PathVariable String groupName, @RequestParam String username) {

    return groupService.addGroupMember(id, groupName, username);
  }

  @DeleteMapping("/{groupName}/removeGroupMember")
  public Group removeGroupMember(
      @RequestParam Long id, @PathVariable String groupName, @RequestParam String username) {

    return groupService.removeGroupMember(id, groupName, username);
  }

  @DeleteMapping("/{groupName}/deleteGroup")
  public ResponseEntity<Group> deleteGroup(@RequestParam Long id, @PathVariable String groupName) {

    return ResponseEntity.ok(groupService.deleteGroup(id, groupName));
  }
}
