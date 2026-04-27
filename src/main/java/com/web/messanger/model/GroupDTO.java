package com.web.messanger.model;

import java.util.Set;

public class GroupDTO {

  private String name;
  private Set<Long> userIds;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Set<Long> getUserIds() {
    return userIds;
  }

  public void setUserIds(Set<Long> userIds) {
    this.userIds = userIds;
  }
}
