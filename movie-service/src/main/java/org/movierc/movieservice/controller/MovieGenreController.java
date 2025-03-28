package org.movierc.movieservice.controller;

import lombok.AllArgsConstructor;
import org.movierc.movieservice.dto.MovieDto;
import org.movierc.movieservice.facade.MovieFacade;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RequestMapping("/api/v1/movie/{movieId}/genre")
@RestController
@AllArgsConstructor
public class MovieGenreController {
    private final MovieFacade movieFacade;

    @PutMapping("/{genreId}")
    public ResponseEntity<MovieDto> assignGenre(@PathVariable UUID movieId, @PathVariable UUID genreId) {
        return ResponseEntity.ok(movieFacade.assignGenre(movieId, genreId));
    }

    @DeleteMapping("/{genreId}")
    public ResponseEntity<MovieDto> removeGenre(@PathVariable UUID movieId, @PathVariable UUID genreId) {
        return ResponseEntity.ok(movieFacade.removeGenre(movieId, genreId));
    }
}
