package com.web.messanger.controller.mapper;

import com.web.messanger.model.ChatMessage;
import com.web.messanger.model.Group;
import com.web.messanger.model.dtos.GroupResponseDTO;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface GroupMapper {

  @Mapping(target = "lastSendMessage", source = ".", qualifiedByName = "verarbeiteGesamtenRecord")
  GroupResponseDTO toGroupDTO(Group group);

  @Named("verarbeiteGesamtenRecord")
  default ChatMessage verarbeiteGesamtenRecord(Group group) {
    List<ChatMessage> list = new ArrayList<>(group.getMessages());
    list.sort(Comparator.comparingLong(ChatMessage::getId).reversed());
    return list.get(0);
  }
}
