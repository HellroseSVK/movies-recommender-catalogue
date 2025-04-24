package org.movierc.movieservice.service;

import lombok.AllArgsConstructor;
import org.movierc.movieservice.entity.Genre;
import org.movierc.movieservice.exception.GenreAlreadyExistsException;
import org.movierc.movieservice.exception.GenreNotFoundException;
import org.movierc.movieservice.repository.GenreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class GenreService {
    private final GenreRepository genreRepository;

    @Transactional
    public List<Genre> getGenres() {
        return genreRepository.findAll();
    }

    @Transactional
    public Genre createGenre(Genre newGenre) {
        genreRepository.findByName(newGenre.getName())
                .ifPresent((genre) -> {
                    throw new GenreAlreadyExistsException(genre);
                });

        return genreRepository.save(newGenre);
    }

    @Transactional
    public Genre deleteGenre(UUID id) {
        var maybeGenre = genreRepository.findById(id);
        if (maybeGenre.isEmpty()) {
            throw new GenreNotFoundException(id);
        }
        var genre = maybeGenre.get();

        genreRepository.deleteById(genre.getId());

        return genre;
    }
}
