package org.movierc.userservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.movierc.userservice.dto.UserCreateDto;
import org.movierc.userservice.entity.User;

import java.util.UUID;

@Mapper(
  imports = {UUID.class}
)
public interface UserCreateDtoMapper {
  @Mapping(target = "id", expression = "java(UUID.randomUUID())")
  User userCreateDtoToUser(UserCreateDto user);
}
