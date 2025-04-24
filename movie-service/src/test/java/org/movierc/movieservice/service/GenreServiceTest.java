package org.movierc.movieservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.movierc.movieservice.entity.Genre;
import org.movierc.movieservice.exception.GenreAlreadyExistsException;
import org.movierc.movieservice.exception.GenreNotFoundException;
import org.movierc.movieservice.mapper.GenreCreateDtoMapper;
import org.movierc.movieservice.mapper.GenreMapper;
import org.movierc.movieservice.rabbit.GenreEventProducer;
import org.movierc.movieservice.repository.GenreRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GenreServiceTest {

    @Mock
    private GenreRepository genreRepository;
    @Mock
    private GenreEventProducer genreEventProducer;

    private final GenreCreateDtoMapper genreCreateDtoMapper = Mappers.getMapper(GenreCreateDtoMapper.class);
    private final GenreMapper genreMapper = Mappers.getMapper(GenreMapper.class);

    private GenreService genreService;

    @BeforeEach
    void setUp() {
        genreService = new GenreService(genreRepository);
    }

    @Test
    void getGenres_WhenGenresExist_ReturnsListOfGenreDtos() {
        // Changed: use get(0) and service.getGenres() now returns entities.
        Genre genre = Genre.builder()
                        .id(UUID.randomUUID())
                        .name("Action")
                        .build();
        when(genreRepository.findAll()).thenReturn(List.of(genre));

        List<Genre> result = genreService.getGenres();

        assertEquals(1, result.size());
        assertEquals(genre.getId(), result.getFirst().getId());
    }

    @Test
    void getGenres_WhenNoGenresExist_ReturnsEmptyList() {
        // Setup
        when(genreRepository.findAll()).thenReturn(List.of());

        // Act
        List<Genre> result = genreService.getGenres();

        // Verify
        assertTrue(result.isEmpty());
    }

    @Test
    void getGenres_WhenRepositoryException_ThrowsException() {
        // Setup
        when(genreRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> genreService.getGenres());
    }


    @Test
    void createGenre_WhenNewGenre_CreatesAndEventProduced() {
        // Changed: pass a Genre instead of GenreCreateDto.
        Genre newGenre = Genre.builder().name("Action").build();

        when(genreRepository.findByName(newGenre.getName())).thenReturn(Optional.empty());
        when(genreRepository.save(any())).thenAnswer(invocation -> {
            Genre arg = invocation.getArgument(0);
            arg.setId(UUID.randomUUID());
            return arg;
        });

        Genre result = genreService.createGenre(newGenre);

        verify(genreRepository).save(any());
        assertNotNull(result);
        assertEquals(newGenre.getName(), result.getName());
    }

    @Test
    void createGenre_WhenDuplicateGenre_ThrowsException() {
        // Changed: pass a Genre instead of GenreCreateDto.
        Genre newGenre = Genre.builder().name("Action").build();

        Genre existingGenre = Genre.builder()
                .id(UUID.randomUUID())
                .name(newGenre.getName())
                .build();

        when(genreRepository.findByName(newGenre.getName())).thenReturn(Optional.of(existingGenre));

        assertThrows(GenreAlreadyExistsException.class, () -> genreService.createGenre(newGenre));
        verify(genreRepository, never()).save(any());
    }

    @Test
    void createGenre_WhenRepositoryException_ThrowsException() {
        // Changed: pass a Genre instead of GenreCreateDto.
        Genre newGenre = Genre.builder().name("Action").build();

        when(genreRepository.findByName(newGenre.getName())).thenReturn(Optional.empty());
        when(genreRepository.save(any())).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> genreService.createGenre(newGenre));
        verify(genreRepository).save(any());
    }

    @Test
    void deleteGenre_WhenGenreExists_DeletesAndEventProduced() {
        // Setup
        UUID genreId = UUID.randomUUID();
        Genre genre = Genre.builder()
                .id(genreId)
                .name("Action")
                .build();

        when(genreRepository.findById(genreId)).thenReturn(Optional.of(genre));

        // Act
        genreService.deleteGenre(genreId);

        // Verify
        verify(genreRepository).deleteById(genreId);
    }

    @Test
    void deleteGenre_WhenGenreNotFound_ThrowsException() {
        // Setup
        UUID genreId = UUID.randomUUID();
        when(genreRepository.findById(genreId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(GenreNotFoundException.class, () -> genreService.deleteGenre(genreId));
        verify(genreRepository, never()).deleteById(any());
    }

    @Test
    void deleteGenre_WhenRepositoryException_ThrowsException() {
        // Setup
        UUID genreId = UUID.randomUUID();
        Genre genre = Genre.builder()
                .id(genreId)
                .name("Action")
                .build();

        when(genreRepository.findById(genreId)).thenReturn(Optional.of(genre));
        doThrow(new RuntimeException("Database error")).when(genreRepository).deleteById(genreId);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> genreService.deleteGenre(genreId));
        verify(genreRepository).deleteById(genreId);
    }
}
