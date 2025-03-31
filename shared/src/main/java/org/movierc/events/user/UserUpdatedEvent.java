package org.movierc.events.user;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Builder
@Getter
@Setter
@ToString
public class UserUpdatedEvent {
    private UUID id;
    private String username;
    private String email;
}
