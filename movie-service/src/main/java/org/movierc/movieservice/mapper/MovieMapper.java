package org.movierc.movieservice.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.movierc.events.movie.MovieCreatedEvent;
import org.movierc.events.movie.MovieDeletedEvent;
import org.movierc.events.movie.MovieFilmPersonRole;
import org.movierc.events.movie.MovieUpdatedEvent;
import org.movierc.movieservice.dto.MovieDto;
import org.movierc.movieservice.entity.FilmPersonInMovieRole;
import org.movierc.movieservice.entity.Movie;

@Mapper(uses = {
        GenreMapper.class
})
public interface MovieMapper {
    MovieDto movieToMovieDto(Movie movie);

    MovieCreatedEvent movieToMovieCreatedEvent(Movie movie);

    @AfterMapping
    default void setMovieFilmPersonMovieId(@MappingTarget MovieCreatedEvent movie) {
        movie.getFilmPersonRoles().forEach(filmPersonRole -> filmPersonRole.setMovieId(movie.getId()));
    }

    MovieUpdatedEvent movieToMovieUpdatedEvent(Movie movie);

    MovieDeletedEvent movieToMovieDeletedEvent(Movie movie);

    @Mapping(target = "movieId", ignore = true)
    @Mapping(target = "filmPersonId", source = "filmPerson.id")
    MovieFilmPersonRole filmPersonInMovieRoleToMovieFilmPersonRole(FilmPersonInMovieRole filmPersonInMovieRole);
}
