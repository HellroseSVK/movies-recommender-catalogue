package org.movierc.feedbackservice.mapper;

import org.mapstruct.Mapper;
import org.movierc.events.user.UserCreatedEvent;
import org.movierc.events.user.UserUpdatedEvent;
import org.movierc.feedbackservice.entity.User;

@Mapper
public interface UserEventMapper {
    User userCreatedEventToUser(UserCreatedEvent userCreatedEvent);

    User userUpdatedEventToUser(UserUpdatedEvent userUpdatedEvent);
}
