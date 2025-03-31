package org.movierc.feedbackservice.mapper;

import org.mapstruct.Mapper;
import org.movierc.feedbackservice.dto.UserDto;
import org.movierc.feedbackservice.entity.User;

@Mapper
public interface UserMapper {
    UserDto userToUserDto(User user);
}
