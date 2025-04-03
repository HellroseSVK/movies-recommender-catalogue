package org.movierc.movieservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.movierc.shared.enm.FilmPersonRole;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieFilmPersonRoleDto {
    @NotNull
    @Valid
    private FilmPersonDto filmPerson;
    @NotNull
    private FilmPersonRole role;
}
