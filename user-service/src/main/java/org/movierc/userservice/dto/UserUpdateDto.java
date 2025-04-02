package org.movierc.userservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDto {
    @NotBlank
    @Schema(example = "superman")
    private String username;
    @NotBlank
    @Email
    @Schema(example = "superman@krypton.com")
    private String email;
    @NotBlank
    @Schema(example = "password")
    private String password;
}
