package org.movierc.events.user;

import lombok.*;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDeletedEvent {
    private UUID id;
    private String username;
}
