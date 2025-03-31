package org.movierc.userservice.entity;

import lombok.*;
import org.movierc.shared.enm.UserRole;

import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private UUID id;
    private String username;
    private String password;
    private String email;
    private List<UserRole> roles;
}
