package com.web.messanger.controller.mapper;

import com.web.messanger.model.User;
import com.web.messanger.model.dtos.UserDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

  UserDTO toUserDTO(User user);
}
