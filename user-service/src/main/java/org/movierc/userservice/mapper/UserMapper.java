package org.movierc.userservice.mapper;

import org.mapstruct.Mapper;
import org.movierc.events.user.UserCreatedEvent;
import org.movierc.events.user.UserDeletedEvent;
import org.movierc.events.user.UserUpdatedEvent;
import org.movierc.userservice.dto.UserDto;
import org.movierc.userservice.entity.User;

@Mapper
public interface UserMapper {
    UserDto userToUserDto(User user);
    UserCreatedEvent userToUserCreatedEvent(User user);
    UserUpdatedEvent userToUserUpdatedEvent(User user);
    UserDeletedEvent userToUserDeletedEvent(User user);
}
