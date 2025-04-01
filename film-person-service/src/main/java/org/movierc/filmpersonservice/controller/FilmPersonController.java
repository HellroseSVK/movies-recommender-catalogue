package org.movierc.filmpersonservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Get all film persons", description = "Returns a list of film persons.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval"),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "text/plain"))
    })
    @GetMapping
    public ResponseEntity<List<FilmPersonDto>> getAllFilmPersons() {
        return ResponseEntity.ok(filmPersonFacade.getAllFilmPersons());
    }

    @Operation(summary = "Get film person by id", description = "Returns a single film person by id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval"),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "404", description = "Film person not found", content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "text/plain"))
    })
    @GetMapping("/{id}")
    public ResponseEntity<FilmPersonDto> getFilmPersonById(@PathVariable UUID id) {
        return ResponseEntity.ok(filmPersonFacade.getFilmPersonById(id));
    }

    @Operation(summary = "Create film person", description = "Creates a new film person.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Film person created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "text/plain"))
    })
    @PostMapping
    public ResponseEntity<FilmPersonDto> createFilmPerson(@RequestBody @Valid FilmPersonCreateDto filmPersonCreateDto) {
        FilmPersonDto createdFilmPerson = filmPersonFacade.createFilmPerson(filmPersonCreateDto);
        return new ResponseEntity<>(createdFilmPerson, HttpStatus.CREATED);
    }

    @Operation(summary = "Update film person", description = "Updates an existing film person by id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Film person updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "404", description = "Film person not found", content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "text/plain"))
    })
    @PutMapping("/{id}")
    public ResponseEntity<FilmPersonDto> updateFilmPerson(@PathVariable UUID id, @RequestBody @Valid FilmPersonUpdateDto filmPersonUpdateDto) {
        return ResponseEntity.ok(filmPersonFacade.updateFilmPerson(id, filmPersonUpdateDto));
    }

    @Operation(summary = "Delete film person", description = "Deletes an existing film person by id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Film person deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "404", description = "Film person not found", content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "text/plain"))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFilmPerson(@PathVariable UUID id) {
        filmPersonFacade.deleteFilmPerson(id);
        return ResponseEntity.noContent().build();
    }
}
