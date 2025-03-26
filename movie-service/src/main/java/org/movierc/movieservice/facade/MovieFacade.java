package org.movierc.movieservice.facade;

import lombok.AllArgsConstructor;
import org.movierc.movieservice.dto.MovieCreateDto;
import org.movierc.movieservice.dto.MovieDto;
import org.movierc.movieservice.dto.MovieUpdateDto;
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
}
