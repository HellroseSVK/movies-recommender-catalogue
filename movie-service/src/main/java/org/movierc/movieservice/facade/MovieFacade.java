package org.movierc.movieservice.facade;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.movierc.movieservice.dto.AssignFilmPersonRoleDto;
import org.movierc.movieservice.dto.MovieCreateDto;
import org.movierc.movieservice.dto.MovieDto;
import org.movierc.movieservice.dto.MovieUpdateDto;
import org.movierc.movieservice.dto.RemoveFilmPersonRoleDto;
import org.movierc.movieservice.service.MovieService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class MovieFacade {
    private final MovieService movieService;

    public List<MovieDto> getAllMovies() {
        return movieService.getAllMovies();
    }

    public MovieDto getMovieById(UUID id) {
        return movieService.getMovieById(id);
    }

    public MovieDto createMovie(MovieCreateDto movieCreateDto) {
        return movieService.createMovie(movieCreateDto);
    }

    public MovieDto updateMovie(UUID id, MovieUpdateDto movieUpdateDto) {
        return movieService.updateMovie(id, movieUpdateDto);
    }

    public void deleteMovie(UUID id) {
        movieService.deleteMovie(id);
    }

    public MovieDto assignGenre(UUID movieId, UUID genreId) {
        return movieService.assignGenre(movieId, genreId);
    }

    public MovieDto removeGenre(UUID movieId, UUID genreId) {
        return movieService.removeGenre(movieId, genreId);
    }

    public MovieDto assignFilmPerson(UUID movieId, @Valid AssignFilmPersonRoleDto assignFilmPersonRoleDto) {
        return movieService.assignFilmPerson(movieId, assignFilmPersonRoleDto);
    }

    public MovieDto removeFilmPerson(UUID movieId, @Valid RemoveFilmPersonRoleDto removeFilmPersonRoleDto) {
        return movieService.removeFilmPerson(movieId, removeFilmPersonRoleDto);
    }
}
