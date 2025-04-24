package org.movierc.movieservice.facade;

import lombok.AllArgsConstructor;
import org.movierc.movieservice.dto.GenreCreateDto;
import org.movierc.movieservice.dto.GenreDto;
import org.movierc.movieservice.mapper.GenreCreateDtoMapper;
import org.movierc.movieservice.mapper.GenreMapper;
import org.movierc.movieservice.rabbit.GenreEventProducer;
import org.movierc.movieservice.service.GenreService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class GenreFacade {
    public final GenreService genreService;

    private final GenreEventProducer genreEventProducer;

    private final GenreCreateDtoMapper genreCreateDtoMapper;
    private final GenreMapper genreMapper;

    public List<GenreDto> getGenres() {
        return genreService.getGenres().stream().map(genreMapper::genreToGenreDto).collect(Collectors.toList());
    }

    public GenreDto createGenre(GenreCreateDto genreDto) {
        var savedGenre = genreService.createGenre(genreCreateDtoMapper.genreCreateDtoToGenre(genreDto));

        genreEventProducer.sendGenreCreatedEvent(genreMapper.genreToGenreCreatedEvent(savedGenre));

        return genreMapper.genreToGenreDto(savedGenre);
    }

    public void deleteGenre(UUID id) {
        var genre = genreService.deleteGenre(id);

        genreEventProducer.sendGenreDeletedEvent(genreMapper.genreToGenreDeletedEvent(genre));
    }
}
