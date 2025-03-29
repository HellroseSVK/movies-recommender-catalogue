package org.movierc.filmpersonservice.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.movierc.filmpersonservice.dto.FilmPersonCreateDto;
import org.movierc.filmpersonservice.dto.FilmPersonDto;
import org.movierc.filmpersonservice.dto.FilmPersonUpdateDto;
import org.movierc.filmpersonservice.facade.FilmPersonFacade;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RequestMapping("/api/v1/filmPerson")
@RestController
@AllArgsConstructor
public class FilmPersonController {
    private final FilmPersonFacade filmPersonFacade;

    @GetMapping
    public ResponseEntity<List<FilmPersonDto>> getAllFilmPersons() {
        return ResponseEntity.ok(filmPersonFacade.getAllFilmPersons());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FilmPersonDto> getFilmPersonById(@PathVariable UUID id) {
        return ResponseEntity.ok(filmPersonFacade.getFilmPersonById(id));
    }

    @PostMapping
    public ResponseEntity<FilmPersonDto> createFilmPerson(@RequestBody @Valid FilmPersonCreateDto filmPersonCreateDto) {
        FilmPersonDto createdFilmPerson = filmPersonFacade.createFilmPerson(filmPersonCreateDto);
        return new ResponseEntity<>(createdFilmPerson, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FilmPersonDto> updateFilmPerson(@PathVariable UUID id, @RequestBody @Valid FilmPersonUpdateDto filmPersonUpdateDto) {
        return ResponseEntity.ok(filmPersonFacade.updateFilmPerson(id, filmPersonUpdateDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFilmPerson(@PathVariable UUID id) {
        filmPersonFacade.deleteFilmPerson(id);
        return ResponseEntity.noContent().build();
    }
}
