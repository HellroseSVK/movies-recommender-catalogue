package org.movierc.movieservice.dto;

import jakarta.validation.Valid;
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
    private FilmPersonDto filmPerson;
    private FilmPersonRole role;
}
