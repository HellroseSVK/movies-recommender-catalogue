package org.movierc.filmpersonservice.facade;

import lombok.AllArgsConstructor;
import org.movierc.filmpersonservice.dto.FilmPersonCreateDto;
import org.movierc.filmpersonservice.dto.FilmPersonDto;
import org.movierc.filmpersonservice.dto.FilmPersonUpdateDto;
import org.movierc.filmpersonservice.mapper.FilmPersonCreateDtoMapper;
import org.movierc.filmpersonservice.mapper.FilmPersonMapper;
import org.movierc.filmpersonservice.rabbit.FilmPersonEventProducer;
import org.movierc.filmpersonservice.service.FilmPersonService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FilmPersonFacade {
    private final FilmPersonService filmPersonService;
    private final FilmPersonMapper filmPersonMapper;
    private final FilmPersonCreateDtoMapper filmPersonCreateDtoMapper;
    private final FilmPersonEventProducer filmPersonEventProducer;

    public List<FilmPersonDto> getAllFilmPersons() {
        return filmPersonService.getAllFilmPersons().stream()
                .map(filmPersonMapper::filmPersonToFilmPersonDto)
                .collect(Collectors.toList());
    }

    public FilmPersonDto getFilmPersonById(UUID id) {
        return filmPersonMapper.filmPersonToFilmPersonDto(filmPersonService.getFilmPersonById(id));
    }

    public FilmPersonDto createFilmPerson(FilmPersonCreateDto filmPersonCreateDto) {
        var savedFilmPerson = filmPersonService.createFilmPerson(filmPersonCreateDtoMapper.filmPersonCreateDtoToMovie(filmPersonCreateDto));
        filmPersonEventProducer.sendFilmPersonCreatedEvent(filmPersonMapper.filmPersonToFilmPersonCreatedEvent(savedFilmPerson));
        return filmPersonMapper.filmPersonToFilmPersonDto(savedFilmPerson);
    }

    public FilmPersonDto updateFilmPerson(UUID id, FilmPersonUpdateDto filmPersonUpdateDto) {
        var updatedFilmPerson = filmPersonService.updateFilmPerson(id, filmPersonUpdateDto);
        filmPersonEventProducer.sendFilmPersonUpdatedEvent(filmPersonMapper.filmPersonToFilmPersonUpdatedEvent(updatedFilmPerson));
        return filmPersonMapper.filmPersonToFilmPersonDto(updatedFilmPerson);
    }

    public void deleteFilmPerson(UUID id) {
        var deletedFilmPerson = filmPersonService.deleteFilmPerson(id);
        filmPersonEventProducer.sendFilmPersonDeletedEvent(filmPersonMapper.filmPersonToFilmPersonDeletedEvent(deletedFilmPerson));

    }
}
