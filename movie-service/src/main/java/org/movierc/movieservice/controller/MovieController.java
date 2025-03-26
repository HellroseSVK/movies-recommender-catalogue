package org.movierc.movieservice.controller;

import lombok.AllArgsConstructor;
import org.movierc.movieservice.dto.MovieCreateDto;
import org.movierc.movieservice.dto.MovieDto;
import org.movierc.movieservice.dto.MovieUpdateDto;
import org.movierc.movieservice.facade.MovieFacade;
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

@RequestMapping("/api/v1/movies")
@RestController
@AllArgsConstructor
public class MovieController {
    private final MovieFacade movieFacade;

    @GetMapping
    public ResponseEntity<List<MovieDto>> getAllMovies() {
        return ResponseEntity.ok(movieFacade.getAllMovies());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovieDto> getMovieById(@PathVariable UUID id) {
        return ResponseEntity.ok(movieFacade.getMovieById(id));
    }

    @PostMapping
    public ResponseEntity<MovieDto> createMovie(@RequestBody MovieCreateDto movieCreateDto) {
        MovieDto createdMovie = movieFacade.createMovie(movieCreateDto);
        return new ResponseEntity<>(createdMovie, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MovieDto> updateMovie(@PathVariable UUID id, @RequestBody MovieUpdateDto movieUpdateDto) {
        return ResponseEntity.ok(movieFacade.updateMovie(id, movieUpdateDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable UUID id) {
        movieFacade.deleteMovie(id);
        return ResponseEntity.noContent().build();
    }
}
