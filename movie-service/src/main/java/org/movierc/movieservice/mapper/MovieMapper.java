package org.movierc.movieservice.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.movierc.events.movie.MovieCreatedEvent;
import org.movierc.events.movie.MovieDeletedEvent;
import org.movierc.events.movie.MovieUpdatedEvent;
import org.movierc.movieservice.dto.FilmPersonDto;
import org.movierc.movieservice.dto.MovieDto;
import org.movierc.movieservice.dto.MovieFilmPersonRoleDto;
import org.movierc.movieservice.entity.FilmPerson;
import org.movierc.movieservice.entity.FilmPersonInMovieRole;
import org.movierc.movieservice.entity.Movie;

@Mapper(uses = {
        MovieMapper.FilmPersonInMovieRoleMapper.class,
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

    @Mapper
    interface FilmPersonInMovieRoleMapper {
        MovieFilmPersonRoleDto filmPersonInMovieRoleToMovieFilmPersonInMovieRole(
                FilmPersonInMovieRole filmPersonInMovieRole
        );

        FilmPersonDto filmPersonToFilmPersonDto(FilmPerson filmPerson);
    }
}
