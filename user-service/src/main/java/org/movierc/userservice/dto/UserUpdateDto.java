package org.movierc.userservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Valid
public class UserUpdateDto {
    @NotBlank
    private String username;
    @NotBlank
    private String email;
    @NotBlank
    private String password;
}
