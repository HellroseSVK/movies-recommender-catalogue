package org.movierc.movieservice.service;

import lombok.AllArgsConstructor;
import org.movierc.movieservice.dto.GenreCreateDto;
import org.movierc.movieservice.dto.GenreDto;
import org.movierc.movieservice.exception.GenreAlreadyExistsException;
import org.movierc.movieservice.exception.GenreNotFoundException;
import org.movierc.movieservice.mapper.GenreCreateDtoMapper;
import org.movierc.movieservice.mapper.GenreMapper;
import org.movierc.movieservice.rabbit.GenreEventProducer;
import org.movierc.movieservice.repository.GenreRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class GenreService {
    private final GenreRepository genreRepository;
    private final GenreEventProducer genreEventProducer;
    private final GenreCreateDtoMapper genreCreateDtoMapper;
    private final GenreMapper genreMapper;

    public List<GenreDto> getGenres() {
        return genreRepository.findAll().stream().map(genreMapper::genreToGenreDto).collect(Collectors.toList());
    }

    public GenreDto createGenre(GenreCreateDto genreDto) {
        var existingGenre = genreRepository.findByName(genreDto.getName());
        existingGenre.ifPresent(genre -> {
            throw new GenreAlreadyExistsException(genre);
        });

        var genre = genreCreateDtoMapper.genreCreateDtoToGenre(genreDto);

        var savedGenre = genreRepository.save(genre);

        genreEventProducer.sendGenreCreatedEvent(genreMapper.genreToGenreCreatedEvent(savedGenre));

        return genreMapper.genreToGenreDto(savedGenre);
    }

    public void deleteGenre(UUID id) {
        var maybeGenre = genreRepository.findById(id);
        if (maybeGenre.isEmpty()) {
            throw new GenreNotFoundException(id);
        }
        var genre = maybeGenre.get();

        genreRepository.deleteById(genre.getId());

        genreEventProducer.sendGenreDeletedEvent(genreMapper.genreToGenreDeletedEvent(genre));
    }
}
