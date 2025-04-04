package org.movierc.userservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    @Valid
    private UUID id;
    @NotBlank
    @Schema(example = "superman")
    private String username;
    @NotBlank
    @Schema(example = "password")
    private String password;
    @NotBlank
    @Schema(example = "superman@krypton.com")
    private String email;
}
