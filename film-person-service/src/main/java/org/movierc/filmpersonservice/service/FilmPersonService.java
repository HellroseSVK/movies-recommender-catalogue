package org.movierc.filmpersonservice.service;

import lombok.AllArgsConstructor;
import org.movierc.filmpersonservice.dto.FilmPersonUpdateDto;
import org.movierc.filmpersonservice.entity.FilmPerson;
import org.movierc.filmpersonservice.exception.FilmPersonNotFoundException;
import org.movierc.filmpersonservice.mapper.FilmPersonMapper;
import org.movierc.filmpersonservice.repository.FilmPersonRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class FilmPersonService {
    private final FilmPersonRepository filmPersonRepository;
    private final FilmPersonMapper filmPersonMapper;

    public List<FilmPerson> getAllFilmPersons() {
        return filmPersonRepository.findAll();
    }

    public FilmPerson getFilmPersonById(UUID id) {
        return filmPersonRepository.findById(id).orElseThrow(() -> new FilmPersonNotFoundException(id));
    }

    public FilmPerson createFilmPerson(FilmPerson filmPerson) {
        return filmPersonRepository.save(filmPerson);
    }

    public FilmPerson updateFilmPerson(UUID id, FilmPersonUpdateDto filmPersonUpdateDto) {
        var filmPerson = filmPersonRepository.findById(id)
                .orElseThrow(() -> new FilmPersonNotFoundException(id));

        filmPersonMapper.updateFilmPerson(filmPersonUpdateDto, filmPerson);
        return filmPersonRepository.save(filmPerson);
    }

    public FilmPerson deleteFilmPerson(UUID id) {
        var filmPerson = filmPersonRepository.findById(id)
                .orElseThrow(() -> new FilmPersonNotFoundException(id));

        filmPersonRepository.deleteById(id);
        return filmPerson;
    }
}
