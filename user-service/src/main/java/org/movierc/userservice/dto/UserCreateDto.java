package org.movierc.userservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.movierc.shared.enm.UserRole;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Valid
public class UserCreateDto {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    @NotBlank
    private String email;
    private List<UserRole> roles;
}
