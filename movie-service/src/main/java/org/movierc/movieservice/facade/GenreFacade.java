package org.movierc.movieservice.facade;

import lombok.AllArgsConstructor;
import org.movierc.movieservice.dto.GenreCreateDto;
import org.movierc.movieservice.dto.GenreDto;
import org.movierc.movieservice.service.GenreService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class GenreFacade {
    public final GenreService genreService;

    public List<GenreDto> getGenres() {
        return genreService.getGenres();
    }

    public GenreDto createGenre(GenreCreateDto genreDto) {
        return genreService.createGenre(genreDto);
    }

    public void deleteGenre(UUID id) {
        genreService.deleteGenre(id);
    }
}
