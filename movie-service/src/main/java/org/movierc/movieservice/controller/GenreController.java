package org.movierc.movieservice.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.movierc.movieservice.dto.GenreCreateDto;
import org.movierc.movieservice.dto.GenreDto;
import org.movierc.movieservice.facade.GenreFacade;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/genre")
@AllArgsConstructor
public class GenreController {
    private final GenreFacade genreFacade;

    @GetMapping
    public ResponseEntity<List<GenreDto>> getGenres() {
        return ResponseEntity.ok(genreFacade.getGenres());
    }

    @PostMapping
    public ResponseEntity<GenreDto> createGenre(@RequestBody @Valid GenreCreateDto genreCreateDto) {
        return ResponseEntity.ok(genreFacade.createGenre(genreCreateDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGenre(@PathVariable UUID id) {
        genreFacade.deleteGenre(id);
        return ResponseEntity.noContent().build();
    }
}
