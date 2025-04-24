package org.movierc.movieservice.facade;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.movierc.events.movie.MovieFilmPersonRole;
import org.movierc.events.movie.MovieFilmPersonRoleAddedEvent;
import org.movierc.events.movie.MovieFilmPersonRoleRemovedEvent;
import org.movierc.movieservice.dto.AssignFilmPersonRoleDto;
import org.movierc.movieservice.dto.MovieCreateDto;
import org.movierc.movieservice.dto.MovieDto;
import org.movierc.movieservice.dto.MovieFilmPersonRoleCreateDto;
import org.movierc.movieservice.dto.MovieUpdateDto;
import org.movierc.movieservice.dto.RemoveFilmPersonRoleDto;
import org.movierc.movieservice.entity.Movie;
import org.movierc.movieservice.mapper.MovieCreateDtoMapper;
import org.movierc.movieservice.mapper.MovieMapper;
import org.movierc.movieservice.rabbit.MovieEventProducer;
import org.movierc.movieservice.rabbit.MovieFilmPersonRoleProducer;
import org.movierc.movieservice.service.MovieService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Transactional
@Service
@AllArgsConstructor
public class MovieFacade {
    private final MovieService movieService;

    private final MovieEventProducer movieEventProducer;
    private final MovieFilmPersonRoleProducer movieFilmPersonRoleProducer;

    private final MovieMapper movieMapper;
    private final MovieCreateDtoMapper movieCreateDtoMapper;

    @Transactional(readOnly = true)
    public List<MovieDto> getAllMovies() {
        return movieService.getAllMovies().stream().map(movieMapper::movieToMovieDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MovieDto getMovieById(UUID id) {
        return movieMapper.movieToMovieDto(movieService.getMovieById(id));
    }

    public MovieDto createMovie(MovieCreateDto movieCreateDto) {
        Movie movie = movieCreateDtoMapper.movieCreateDtoToMovie(movieCreateDto);

        var savedMovie = movieService.createMovie(
                movie,
                movieCreateDto.getGenres(),
                movieCreateDto
                        .getFilmPersonRoles()
                        .stream()
                        .collect(Collectors.toMap(
                                MovieFilmPersonRoleCreateDto::getFilmPersonId,
                                MovieFilmPersonRoleCreateDto::getRole
                        ))
        );

        movieEventProducer.sendMovieCreatedEvent(movieMapper.movieToMovieCreatedEvent(savedMovie));

        return movieMapper.movieToMovieDto(savedMovie);
    }

    public MovieDto updateMovie(UUID id, MovieUpdateDto movieUpdateDto) {
        var updatedMovie = movieService.updateMovie(
                id,
                movieUpdateDto.getTitle(),
                movieUpdateDto.getDescription(),
                movieUpdateDto.getImages()
        );

        movieEventProducer.sendMovieUpdatedEvent(movieMapper.movieToMovieUpdatedEvent(updatedMovie));

        return movieMapper.movieToMovieDto(updatedMovie);
    }

    public void deleteMovie(UUID id) {
        var movie = movieService.deleteMovie(id);

        movieEventProducer.sendMovieDeletedEvent(movieMapper.movieToMovieDeletedEvent(movie));
    }

    public MovieDto assignGenre(UUID movieId, UUID genreId) {
        var updatedMovie = movieService.assignGenre(movieId, genreId);
        movieEventProducer.sendMovieUpdatedEvent(movieMapper.movieToMovieUpdatedEvent(updatedMovie));
        return movieMapper.movieToMovieDto(updatedMovie);
    }

    public MovieDto removeGenre(UUID movieId, UUID genreId) {
        var updatedMovie = movieService.removeGenre(movieId, genreId);
        movieEventProducer.sendMovieUpdatedEvent(movieMapper.movieToMovieUpdatedEvent(updatedMovie));
        return movieMapper.movieToMovieDto(updatedMovie);
    }

    public MovieDto assignFilmPerson(UUID movieId, @Valid AssignFilmPersonRoleDto assignFilmPersonRoleDto) {
        var updatedMovie = movieService.assignFilmPerson(
                movieId,
                assignFilmPersonRoleDto.getFilmPersonId(),
                assignFilmPersonRoleDto.getRole()
        );

        movieFilmPersonRoleProducer.sendMovieFilmPersonRoleAddedEvent(
                new MovieFilmPersonRoleAddedEvent(MovieFilmPersonRole.builder()
                        .filmPersonId(assignFilmPersonRoleDto.getFilmPersonId())
                        .role(assignFilmPersonRoleDto.getRole())
                        .movieId(movieId).build())
        );

        return movieMapper.movieToMovieDto(updatedMovie);
    }

    public MovieDto removeFilmPerson(UUID movieId, @Valid RemoveFilmPersonRoleDto removeFilmPersonRoleDto) {
        var updatedMovie = movieService.removeFilmPerson(
                movieId,
                removeFilmPersonRoleDto.getFilmPersonId(),
                removeFilmPersonRoleDto.getRole()
        );

        movieFilmPersonRoleProducer.sendMovieFilmPersonRoleRemovedEvent(
                new MovieFilmPersonRoleRemovedEvent(MovieFilmPersonRole.builder()
                        .filmPersonId(removeFilmPersonRoleDto.getFilmPersonId())
                        .role(removeFilmPersonRoleDto.getRole())
                        .movieId(movieId).build())
        );

        return movieMapper.movieToMovieDto(updatedMovie);
    }
}
