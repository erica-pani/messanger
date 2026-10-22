package com.web.messanger.controller.mapper;

import com.web.messanger.model.User;
import com.web.messanger.model.dtos.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

  @Mapping(source = "hashed_password", target = "password")
  UserDTO toUserDTO(User user);
}
