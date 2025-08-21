package com.atena.core.auth.mapper;

import com.atena.core.auth.dto.NewUserCreatedDTO;
import com.atena.core.auth.dto.UserDTO;
import com.atena.core.auth.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "jakarta-cdi")
public interface UserMapper {

  @Mapping(target = "username", source = "user.name")
  NewUserCreatedDTO toUserCreatedDTO(User user);

  User toUser(UserDTO userDTO);
}
