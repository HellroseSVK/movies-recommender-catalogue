package org.movierc.filmpersonservice.service;

import lombok.AllArgsConstructor;
import org.movierc.filmpersonservice.dto.FilmPersonCreateDto;
import org.movierc.filmpersonservice.dto.FilmPersonDto;
import org.movierc.filmpersonservice.dto.FilmPersonUpdateDto;
import org.movierc.filmpersonservice.entity.FilmPerson;
import org.movierc.filmpersonservice.exception.FilmPersonNotFoundException;
import org.movierc.filmpersonservice.exception.MovieNotFoundException;
import org.movierc.filmpersonservice.mapper.FilmPersonCreateDtoMapper;
import org.movierc.filmpersonservice.mapper.FilmPersonMapper;
import org.movierc.filmpersonservice.rabbit.FilmPersonEventProducer;
import org.movierc.filmpersonservice.repository.FilmPersonRepository;
import org.movierc.filmpersonservice.repository.MovieRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FilmPersonService {
    private final FilmPersonRepository filmPersonRepository;
    private final MovieRepository movieRepository;
    private final FilmPersonEventProducer filmPersonEventProducer;
    private final FilmPersonMapper filmPersonMapper;
    private final FilmPersonCreateDtoMapper filmPersonCreateDtoMapper;

    public List<FilmPersonDto> getAllFilmPersons() {
        return filmPersonRepository.findAll().stream()
                .map(filmPersonMapper::filmPersonToFilmPersonDto)
                .collect(Collectors.toList());
    }

    public FilmPersonDto getFilmPersonById(UUID id) {
        return filmPersonRepository.findById(id)
                .map(filmPersonMapper::filmPersonToFilmPersonDto)
                .orElseThrow(() -> new FilmPersonNotFoundException(id));
    }

    public FilmPersonDto createFilmPerson(FilmPersonCreateDto filmPersonCreateDto) {
        var filmPerson = filmPersonCreateDtoMapper.filmPersonCreateDtoToMovie(filmPersonCreateDto);

        var savedFilmPerson = filmPersonRepository.save(filmPerson);

        filmPersonEventProducer.sendFilmPersonCreatedEvent(filmPersonMapper.filmPersonToFilmPersonCreatedEvent(savedFilmPerson));

        return filmPersonMapper.filmPersonToFilmPersonDto(savedFilmPerson);
    }

    public FilmPersonDto updateFilmPerson(UUID id, FilmPersonUpdateDto filmPersonCreateDto) {
        var filmPerson = filmPersonRepository.findById(id)
                .orElseThrow(() -> new FilmPersonNotFoundException(id));

        filmPerson.setName(filmPersonCreateDto.getName());
        filmPerson.setImage(filmPersonCreateDto.getImage());
        filmPerson.setDateOfBirth(filmPersonCreateDto.getDateOfBirth());
        filmPerson.setBiography(filmPersonCreateDto.getBiography());

        var savedFilmPerson = filmPersonRepository.save(filmPerson);

        filmPersonEventProducer.sendFilmPersonUpdatedEvent(filmPersonMapper.filmPersonToFilmPersonUpdatedEvent(filmPerson));

        return filmPersonMapper.filmPersonToFilmPersonDto(savedFilmPerson);
    }

    public void deleteFilmPerson(UUID id) {
        var filmPerson = filmPersonRepository.findById(id)
                .orElseThrow(() -> new FilmPersonNotFoundException(id));

        filmPersonRepository.deleteById(id);

        filmPersonEventProducer.sendFilmPersonDeletedEvent(filmPersonMapper.filmPersonToFilmPersonDeletedEvent(filmPerson));
    }
}
