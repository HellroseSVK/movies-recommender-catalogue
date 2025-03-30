package org.movierc.filmpersonservice.facade;

import lombok.AllArgsConstructor;
import org.movierc.filmpersonservice.dto.FilmPersonCreateDto;
import org.movierc.filmpersonservice.dto.FilmPersonDto;
import org.movierc.filmpersonservice.dto.FilmPersonUpdateDto;
import org.movierc.filmpersonservice.service.FilmPersonService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class FilmPersonFacade {
    private final FilmPersonService filmPersonService;

    public List<FilmPersonDto> getAllFilmPersons() {
        return filmPersonService.getAllFilmPersons();
    }

    public FilmPersonDto getFilmPersonById(UUID id) {
        return filmPersonService.getFilmPersonById(id);
    }

    public FilmPersonDto createFilmPerson(FilmPersonCreateDto filmPersonCreateDto) {
        return filmPersonService.createFilmPerson(filmPersonCreateDto);
    }

    public FilmPersonDto updateFilmPerson(UUID id, FilmPersonUpdateDto filmPersonUpdateDto) {
        return filmPersonService.updateFilmPerson(id, filmPersonUpdateDto);
    }

    public void deleteFilmPerson(UUID id) {
        filmPersonService.deleteFilmPerson(id);
    }
}
