package org.movierc.filmpersonservice.dto;

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
    private UUID movieId;
    private FilmPersonRole role;
}
