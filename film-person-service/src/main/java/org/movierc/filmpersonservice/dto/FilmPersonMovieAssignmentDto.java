package org.movierc.filmpersonservice.dto;

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
public class FilmPersonMovieAssignmentDto {
    @NotNull
    @Valid
    private UUID movieId;
    @NotNull
    private FilmPersonRole role;
}
