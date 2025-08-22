package com.atena.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "jakarta-cdi")
public interface UserMapper {

  @Mapping(target = "username", source = "user.name")
  NewUserCreatedDTO toUserCreatedDTO(User user);

  User toUser(UserDTO userDTO);
}
