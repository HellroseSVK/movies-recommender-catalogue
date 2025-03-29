package org.movierc.filmpersonservice.mapper;

import org.mapstruct.Mapper;
import org.movierc.events.movie.MovieFilmPersonRole;
import org.movierc.filmpersonservice.entity.FilmPersonMovieAssignment;

@Mapper
public interface FilmPersonMovieAssignmentMapper {
    FilmPersonMovieAssignment movieFilmPersonRoleToFilmPersonMovieAssignment(MovieFilmPersonRole filmPersonMovie);
}
