package org.movierc.events.user;

import lombok.*;
import org.movierc.shared.enm.UserRole;

import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdatedEvent {
    private UUID id;
    private String username;
    private String email;
    private String password;
    private List<UserRole> roles;
}
