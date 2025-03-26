package org.movierc.movieservice.service;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.movierc.events.movie.MovieCreatedEvent;
import org.movierc.movieservice.dto.MovieCreateDto;
import org.movierc.movieservice.dto.MovieDto;
import org.movierc.movieservice.dto.MovieUpdateDto;
import org.movierc.movieservice.entity.Genre;
import org.movierc.movieservice.entity.Movie;
import org.movierc.movieservice.exception.FilmPersonNotFoundException;
import org.movierc.movieservice.exception.GenreNotFoundException;
import org.movierc.movieservice.exception.MovieNotFoundException;
import org.movierc.movieservice.jms.MovieEventProducer;
import org.movierc.movieservice.repository.FilmPersonRepository;
import org.movierc.movieservice.repository.GenreRepository;
import org.movierc.movieservice.repository.MovieRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MovieService {
    private final GenreRepository genreRepository;
    private final FilmPersonRepository filmPersonRepository;
    private final MovieRepository movieRepository;
    private final MovieEventProducer movieEventProducer;
    private final ModelMapper modelMapper;

    public List<MovieDto> getAllMovies() {
        return movieRepository.findAll().stream()
                .map(m -> modelMapper.map(m, MovieDto.class))
                .collect(Collectors.toList());
    }

    public MovieDto getMovieById(UUID id) {
        return movieRepository.findById(id)
                .map(m -> modelMapper.map(m, MovieDto.class))
                .orElseThrow(() -> new MovieNotFoundException(id));
    }

    public MovieDto createMovie(MovieCreateDto movieCreateDto) {
        Movie movie = modelMapper.map(movieCreateDto, Movie.class);

        movie.setGenres(findGenres(movieCreateDto));
        setFilmPersons(movie, movieCreateDto);

        Movie savedMovie = movieRepository.save(movie);

        movieEventProducer.sendMovieCreatedEvent(modelMapper.map(savedMovie, MovieCreatedEvent.class));

        return modelMapper.map(savedMovie, MovieDto.class);
    }

    private List<Genre> findGenres(MovieCreateDto movieCreateDto) {
        return movieCreateDto
                .getGenres()
                .stream()
                .map(genreId -> genreRepository.findById(genreId)
                        .orElseThrow(() -> new GenreNotFoundException(genreId))
                )
                .toList();
    }

    private void setFilmPersons(Movie movie, MovieCreateDto movieCreateDto) {
        movie.setCast(
                movieCreateDto.getCast()
                        .stream()
                        .map(filmPersonId -> filmPersonRepository.findById(filmPersonId)
                                .orElseThrow(() -> new FilmPersonNotFoundException(filmPersonId))
                        )
                        .toList()
        );
        movie.setDirector(
                filmPersonRepository
                        .findById(movieCreateDto.getDirector())
                        .orElseThrow(() -> new FilmPersonNotFoundException(movieCreateDto.getDirector()))
        );
        movie.setScreenwriter(
                filmPersonRepository
                        .findById(movieCreateDto.getScreenwriter())
                        .orElseThrow(() -> new FilmPersonNotFoundException(movieCreateDto.getScreenwriter()))
        );
        movie.setCinematographer(
                filmPersonRepository
                        .findById(movieCreateDto.getCinematographer())
                        .orElseThrow(() -> new FilmPersonNotFoundException(movieCreateDto.getCinematographer()))
        );
        movie.setComposer(
                filmPersonRepository
                        .findById(movieCreateDto.getComposer())
                        .orElseThrow(() -> new FilmPersonNotFoundException(movieCreateDto.getComposer()))
        );
        movie.setSourceMaterialAuthor(
                filmPersonRepository
                        .findById(movieCreateDto.getSourceMaterialAuthor())
                        .orElseThrow(() -> new FilmPersonNotFoundException(movieCreateDto.getSourceMaterialAuthor()))
        );
    }

    public MovieDto updateMovie(UUID id, MovieUpdateDto movieUpdateDto) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new MovieNotFoundException(id));

        movie.setTitle(movieUpdateDto.getTitle());
        movie.setDescription(movieUpdateDto.getDescription());
        movie.setImages(movieUpdateDto.getImages());
        movie.setGenres(movieUpdateDto.getGenres());

        Movie updatedMovie = movieRepository.save(movie);
        return modelMapper.map(updatedMovie, MovieDto.class);
    }

    public void deleteMovie(UUID id) {
        if (!movieRepository.existsById(id)) {
            throw new MovieNotFoundException(id);
        }
        movieRepository.deleteById(id);
    }
}
