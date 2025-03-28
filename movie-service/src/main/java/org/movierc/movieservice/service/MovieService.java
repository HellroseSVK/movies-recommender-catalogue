package org.movierc.movieservice.service;

import lombok.AllArgsConstructor;
import org.movierc.movieservice.dto.MovieCreateDto;
import org.movierc.movieservice.dto.MovieDto;
import org.movierc.movieservice.dto.MovieUpdateDto;
import org.movierc.movieservice.entity.Genre;
import org.movierc.movieservice.entity.Movie;
import org.movierc.movieservice.exception.FilmPersonNotFoundException;
import org.movierc.movieservice.exception.GenreNotFoundException;
import org.movierc.movieservice.exception.MovieNotFoundException;
import org.movierc.movieservice.mapper.MovieCreateDtoMapper;
import org.movierc.movieservice.mapper.MovieMapper;
import org.movierc.movieservice.rabbit.MovieEventProducer;
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
    private final MovieMapper movieMapper;
    private final MovieCreateDtoMapper movieCreateDtoMapper;

    public List<MovieDto> getAllMovies() {
        return movieRepository.findAll().stream()
                .map(movieMapper::movieToMovieDto)
                .collect(Collectors.toList());
    }

    public MovieDto getMovieById(UUID id) {
        return movieRepository.findById(id)
                .map(movieMapper::movieToMovieDto)
                .orElseThrow(() -> new MovieNotFoundException(id));
    }

    public MovieDto createMovie(MovieCreateDto movieCreateDto) {
        Movie movie = movieCreateDtoMapper.movieCreateDtoToMovie(movieCreateDto);

        movie.setGenres(findGenres(movieCreateDto.getGenres()));
        setFilmPersons(movie, movieCreateDto);

        Movie savedMovie = movieRepository.save(movie);

        movieEventProducer.sendMovieCreatedEvent(movieMapper.movieToMovieCreatedEvent(savedMovie));

        return movieMapper.movieToMovieDto(savedMovie);
    }


    public MovieDto updateMovie(UUID id, MovieUpdateDto movieUpdateDto) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new MovieNotFoundException(id));

        movie.setTitle(movieUpdateDto.getTitle());
        movie.setDescription(movieUpdateDto.getDescription());
        movie.setImages(movieUpdateDto.getImages());

        Movie updatedMovie = movieRepository.save(movie);

        movieEventProducer.sendMovieUpdatedEvent(movieMapper.movieToMovieUpdatedEvent(updatedMovie));

        return movieMapper.movieToMovieDto(updatedMovie);
    }

    public void deleteMovie(UUID id) {
        var movie = movieRepository.findById(id);
        if (movie.isEmpty()) {
            throw new MovieNotFoundException(id);
        }

        movieRepository.deleteById(id);

        movieEventProducer.sendMovieDeletedEvent(movieMapper.movieToMovieDeletedEvent(movie.get()));
    }

    private List<Genre> findGenres(List<UUID> genreIds) {
        return genreIds
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

    public MovieDto assignGenre(UUID movieId, UUID genreId) {
        var maybeMovie = movieRepository.findById(movieId);
        if (maybeMovie.isEmpty()) {
            throw new MovieNotFoundException(movieId);
        }
        var movie = maybeMovie.get();

        var maybeGenre = genreRepository.findById(genreId);
        if (maybeGenre.isEmpty()) {
            throw new GenreNotFoundException(genreId);
        }
        var genre = maybeGenre.get();

        movie.getGenres().add(genre);

        var updatedMovie = movieRepository.save(movie);
        movieEventProducer.sendMovieUpdatedEvent(movieMapper.movieToMovieUpdatedEvent(updatedMovie));
        return movieMapper.movieToMovieDto(updatedMovie);
    }

    public MovieDto removeGenre(UUID movieId, UUID genreId) {
        var maybeMovie = movieRepository.findById(movieId);
        if (maybeMovie.isEmpty()) {
            throw new MovieNotFoundException(movieId);
        }
        var movie = maybeMovie.get();

        movie.setGenres(movie.getGenres().stream().filter(g -> !g.getId().equals(genreId)).toList());

        var updatedMovie = movieRepository.save(movie);
        movieEventProducer.sendMovieUpdatedEvent(movieMapper.movieToMovieUpdatedEvent(updatedMovie));
        return movieMapper.movieToMovieDto(updatedMovie);
    }
}
