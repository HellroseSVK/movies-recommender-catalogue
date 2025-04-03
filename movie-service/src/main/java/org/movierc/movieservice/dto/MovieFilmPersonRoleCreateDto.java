package org.movierc.movieservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.movierc.shared.enm.FilmPersonRole;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieFilmPersonRoleCreateDto {
    @NotNull
    private UUID filmPersonId;
    @NotNull
    private FilmPersonRole role;
}
