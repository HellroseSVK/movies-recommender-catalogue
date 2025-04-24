package org.movierc.userservice.mapper;

import org.mapstruct.Mapper;
import org.movierc.userservice.dto.UserCreateDto;
import org.movierc.userservice.entity.User;

import java.util.UUID;

@Mapper(
        imports = {UUID.class}
)
public interface UserCreateDtoMapper {
    User userCreateDtoToUser(UserCreateDto user);
}
