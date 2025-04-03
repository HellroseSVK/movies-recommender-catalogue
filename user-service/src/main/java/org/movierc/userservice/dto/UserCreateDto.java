package org.movierc.userservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
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
public class UserCreateDto {
    @NotBlank
    @Schema(example = "superman")
    private String username;
    @NotBlank
    @Schema(example = "password")
    private String password;
    @NotBlank
    @Schema(example = "superman@krypton.com")
    private String email;
    @NotEmpty
    private List<UserRole> roles;
}
