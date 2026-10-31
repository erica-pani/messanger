package com.web.messanger.controller.mapper;

import com.web.messanger.model.Group;
import com.web.messanger.model.dtos.GroupResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GroupMapper {

  GroupResponseDTO toGroupDTO(Group group);
}
