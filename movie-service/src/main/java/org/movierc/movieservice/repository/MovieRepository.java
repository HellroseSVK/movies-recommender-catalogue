package org.movierc.movieservice.repository;

import org.movierc.movieservice.entity.Movie;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class MovieRepository {
    private final List<Movie> movies = new ArrayList<>();

    public List<Movie> findAll() {
        return new ArrayList<>(movies);
    }

    public Optional<Movie> findById(UUID id) {
        return movies.stream().filter(m -> m.getId().equals(id)).findFirst();
    }

    public boolean existsById(UUID id) {
        return movies.stream().anyMatch(m -> m.getId().equals(id));
    }

    public void deleteById(UUID id) {
        movies.removeIf(m -> m.getId().equals(id));
    }

    public Movie save(Movie movie) {
        if (movies.stream().anyMatch(m -> m.getId().equals(movie.getId()))) {
            movies.removeIf(m -> m.getId().equals(movie.getId()));
        }

        movies.add(movie);
        return movie;
    }

}
