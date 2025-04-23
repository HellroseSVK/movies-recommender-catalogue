package org.movierc.movieservice.service;

import lombok.AllArgsConstructor;
import org.movierc.movieservice.entity.Genre;
import org.movierc.movieservice.exception.GenreAlreadyExistsException;
import org.movierc.movieservice.exception.GenreNotFoundException;
import org.movierc.movieservice.mapper.GenreCreateDtoMapper;
import org.movierc.movieservice.mapper.GenreMapper;
import org.movierc.movieservice.rabbit.GenreEventProducer;
import org.movierc.movieservice.repository.GenreRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class GenreService {
    private final GenreRepository genreRepository;
    private final GenreEventProducer genreEventProducer;
    private final GenreCreateDtoMapper genreCreateDtoMapper;
    private final GenreMapper genreMapper;

    public List<Genre> getGenres() {
        return genreRepository.findAll();
    }

    public Genre createGenre(Genre newGenre) {
        var existingGenre = genreRepository.findByName(newGenre.getName());
        existingGenre.ifPresent(genre -> {
            throw new GenreAlreadyExistsException(genre);
        });

        var savedGenre = genreRepository.save(newGenre);

        genreEventProducer.sendGenreCreatedEvent(genreMapper.genreToGenreCreatedEvent(savedGenre));

        return savedGenre;
    }

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
