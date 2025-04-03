package org.movierc.filmpersonservice.dto;

import jakarta.validation.Valid;
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
public class FilmPersonMovieAssignmentDto {
    @NotNull
    @Valid
    private MovieDto movie;
    @NotNull
    private FilmPersonRole role;
}
