package org.movierc.movieservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.movierc.events.genre.GenreCreatedEvent;
import org.movierc.events.genre.GenreDeletedEvent;
import org.movierc.movieservice.dto.GenreCreateDto;
import org.movierc.movieservice.dto.GenreDto;
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
        genreService = new GenreService(genreRepository, genreEventProducer, genreCreateDtoMapper, genreMapper);
    }

    @Test
    void getGenres_WhenGenresExist_ReturnsListOfGenreDtos() {
        // Setup
        Genre genre = Genre.builder()
                        .id(UUID.randomUUID())
                        .name("Action")
                        .build();
        when(genreRepository.findAll()).thenReturn(List.of(genre));

        // Act
        List<GenreDto> result = genreService.getGenres();

        // Verify
        assertEquals(1, result.size());
        assertEquals(genre.getId(), result.getFirst().getId());
    }

    @Test
    void getGenres_WhenNoGenresExist_ReturnsEmptyList() {
        // Setup
        when(genreRepository.findAll()).thenReturn(List.of());

        // Act
        List<GenreDto> result = genreService.getGenres();

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
        // Setup
        GenreCreateDto genreCreateDto = new GenreCreateDto();
        genreCreateDto.setName("Action");

        when(genreRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        GenreDto result = genreService.createGenre(genreCreateDto);

        // Verify
        verify(genreRepository).save(any());
        assertNotNull(result);
        assertEquals(genreCreateDto.getName(), result.getName());

        var createdEventCaptor = ArgumentCaptor.forClass(GenreCreatedEvent.class);
        verify(genreEventProducer).sendGenreCreatedEvent(createdEventCaptor.capture());
        GenreCreatedEvent createdEvent = createdEventCaptor.getValue();
        assertNotNull(createdEvent);
        assertEquals(result.getId(), createdEvent.getId());
        assertEquals(result.getName(), createdEvent.getName());
    }

    @Test
    void createGenre_WhenDuplicateGenre_ThrowsException() {
        // Setup
        GenreCreateDto genreCreateDto = new GenreCreateDto();
        genreCreateDto.setName("Action");

        Genre existingGenre = Genre.builder()
                .id(UUID.randomUUID())
                .name(genreCreateDto.getName())
                .build();

        when(genreRepository.findByName(genreCreateDto.getName())).thenReturn(Optional.of(existingGenre));

        // Act & Assert
        assertThrows(GenreAlreadyExistsException.class, () -> genreService.createGenre(genreCreateDto));
        verify(genreRepository, never()).save(any());
    }

    @Test
    void createGenre_WhenRepositoryException_ThrowsException() {
        // Setup
        GenreCreateDto genreCreateDto = new GenreCreateDto();
        genreCreateDto.setName("Action");

        when(genreRepository.findByName(genreCreateDto.getName())).thenReturn(Optional.empty());
        when(genreRepository.save(any())).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> genreService.createGenre(genreCreateDto));
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
        var deletedEventCaptor = ArgumentCaptor.forClass(GenreDeletedEvent.class);
        verify(genreEventProducer).sendGenreDeletedEvent(deletedEventCaptor.capture());
        GenreDeletedEvent deletedEvent = deletedEventCaptor.getValue();
        assertNotNull(deletedEvent);
        assertEquals(genre.getId(), deletedEvent.getId());
        assertEquals(genre.getName(), deletedEvent.getName());
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
