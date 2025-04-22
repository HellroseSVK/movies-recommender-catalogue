package org.movierc.movieservice.service;

import lombok.AllArgsConstructor;
import org.movierc.movieservice.entity.FilmPersonInMovieRole;
import org.movierc.movieservice.entity.Genre;
import org.movierc.movieservice.entity.Movie;
import org.movierc.movieservice.exception.FilmPersonNotFoundException;
import org.movierc.movieservice.exception.FilmPersonWithThisRoleIsAlreadyAssigned;
import org.movierc.movieservice.exception.GenreNotFoundException;
import org.movierc.movieservice.exception.MovieNotFoundException;
import org.movierc.movieservice.repository.FilmPersonRepository;
import org.movierc.movieservice.repository.GenreRepository;
import org.movierc.movieservice.repository.MovieRepository;
import org.movierc.shared.enm.FilmPersonRole;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MovieService {
    private final GenreRepository genreRepository;
    private final FilmPersonRepository filmPersonRepository;
    private final MovieRepository movieRepository;

    public List<Movie> getAllMovies() {
        return movieRepository.findAll().stream().toList();
    }

    public Movie getMovieById(UUID id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new MovieNotFoundException(id));
    }

    public Movie createMovie(
            Movie movie,
            List<UUID> genreIds,
            Map<UUID, FilmPersonRole> filmPersonRoles
    ) {
        movie.setGenres(findGenres(genreIds));
        setFilmPersons(movie, filmPersonRoles);

        movieRepository.save(movie);

        return movieRepository.save(movie);
    }


    public Movie updateMovie(UUID id, String title, String description, List<URL> images) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new MovieNotFoundException(id));

        movie.setTitle(title);
        movie.setDescription(description);
        movie.setImages(images);

        return movieRepository.save(movie);
    }

    public Movie deleteMovie(UUID id) {
        var movie = movieRepository.findById(id);
        if (movie.isEmpty()) {
            throw new MovieNotFoundException(id);
        }

        movieRepository.deleteById(id);

        return movie.get();
    }

    private List<Genre> findGenres(List<UUID> genreIds) {
        return genreIds
                .stream()
                .map(genreId -> genreRepository.findById(genreId)
                        .orElseThrow(() -> new GenreNotFoundException(genreId))
                )
                .collect(Collectors.toList());
    }

    private void setFilmPersons(Movie movie, Map<UUID, FilmPersonRole> filmPersonToRole) {
        var resolvedFilmPersonRoles = filmPersonToRole
                .entrySet()
                .stream()
                .map(filmPersonRole ->
                        new FilmPersonInMovieRole(
                                movie,
                                filmPersonRepository.findById(filmPersonRole.getKey())
                                        .orElseThrow(() -> new FilmPersonNotFoundException(filmPersonRole.getKey())),
                                filmPersonRole.getValue()
                        )
                )
                .collect(Collectors.toCollection(ArrayList::new));

        movie.setFilmPersonRoles(resolvedFilmPersonRoles);
    }

    public Movie assignGenre(UUID movieId, UUID genreId) {
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

        return movieRepository.save(movie);
    }

    public Movie removeGenre(UUID movieId, UUID genreId) {
        var maybeMovie = movieRepository.findById(movieId);
        if (maybeMovie.isEmpty()) {
            throw new MovieNotFoundException(movieId);
        }
        var movie = maybeMovie.get();

        movie.setGenres(
                movie.getGenres()
                        .stream()
                        .filter(g -> !g.getId().equals(genreId))
                        .collect(Collectors.toCollection(ArrayList::new))
        );

        return movieRepository.save(movie);
    }

    public Movie assignFilmPerson(UUID movieId, UUID personId, FilmPersonRole filmPersonRole) {
        var maybeMovie = movieRepository.findById(movieId);
        if (maybeMovie.isEmpty()) {
            throw new MovieNotFoundException(movieId);
        }
        var movie = maybeMovie.get();

        var maybeFilmPerson = filmPersonRepository.findById(personId);
        if (maybeFilmPerson.isEmpty()) {
            throw new FilmPersonNotFoundException(personId);
        }
        var filmPerson = maybeFilmPerson.get();

        var isAlreadyAssigned = movie.getFilmPersonRoles().stream()
                .anyMatch(fpr ->
                        fpr.getFilmPerson()
                                .getId()
                                .equals(filmPerson.getId())
                                && fpr.getRole().equals(filmPersonRole)
                );
        if (isAlreadyAssigned) {
            throw new FilmPersonWithThisRoleIsAlreadyAssigned(filmPerson, filmPersonRole);
        }

        movie.getFilmPersonRoles().add(new FilmPersonInMovieRole(
                movie,
                filmPerson,
                filmPersonRole
        ));

        return movieRepository.save(movie);
    }

    public Movie removeFilmPerson(UUID movieId, UUID personId, FilmPersonRole filmPersonRole) {
        var maybeMovie = movieRepository.findById(movieId);
        if (maybeMovie.isEmpty()) {
            throw new MovieNotFoundException(movieId);
        }
        var movie = maybeMovie.get();

        var maybeFilmPerson = filmPersonRepository.findById(personId);
        if (maybeFilmPerson.isEmpty()) {
            throw new FilmPersonNotFoundException(personId);
        }

        movie.setFilmPersonRoles(
                movie.getFilmPersonRoles()
                        .stream()
                        .filter(fpr ->
                                fpr.getFilmPerson()
                                        .getId()
                                        .equals(personId)
                                        && fpr.getRole().equals(filmPersonRole)
                        )
                        .collect(Collectors.toCollection(ArrayList::new))
        );

        return movieRepository.save(movie);
    }
}
