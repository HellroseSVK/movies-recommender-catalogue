package org.movierc.movieservice.facade;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.movierc.movieservice.dto.GenreCreateDto;
import org.movierc.movieservice.dto.GenreDto;
import org.movierc.movieservice.entity.Genre;
import org.movierc.movieservice.exception.GenreNotFoundException;
import org.movierc.movieservice.mapper.GenreCreateDtoMapper;
import org.movierc.movieservice.mapper.GenreMapper;
import org.movierc.movieservice.service.GenreService;
import org.movierc.movieservice.rabbit.GenreEventProducer;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GenreFacadeTest {

    @Mock
    private GenreService genreService;

    @Mock
    private GenreEventProducer genreEventProducer;

    private final GenreMapper genreMapper = Mappers.getMapper(GenreMapper.class);
    private final GenreCreateDtoMapper genreCreateDtoMapper = Mappers.getMapper(GenreCreateDtoMapper.class);

    private GenreFacade genreFacade;

    @BeforeEach
    void setUp() {
        genreFacade = new GenreFacade(genreService, genreEventProducer, genreCreateDtoMapper, genreMapper);
    }

    @Test
    void getGenres_GenresExist_ReturnsListOfGenreDtos() {
        // Setup
        Genre genre = Genre.builder()
                .id(UUID.randomUUID())
                .name("Comedy")
                .build();
        when(genreService.getGenres()).thenReturn(List.of(genre));

        // Act
        List<GenreDto> result = genreFacade.getGenres();

        // Verify
        assertEquals(1, result.size());
        assertEquals("Comedy", result.getFirst().getName());
        verify(genreService).getGenres();
    }

    @Test
    void getGenres_NoGenresFound_ReturnsEmptyList() {
        // Setup
        when(genreService.getGenres()).thenReturn(List.of());

        // Act
        List<GenreDto> result = genreFacade.getGenres();

        // Verify
        assertTrue(result.isEmpty());
        verify(genreService).getGenres();
    }

    @Test
    void createGenre_ValidGenre_ReturnsCreatedGenreDto() {
        // Setup
        GenreCreateDto createDto = GenreCreateDto.builder()
                .name("Horror")
                .build();
        Genre createdGenre = Genre.builder()
                .id(UUID.randomUUID())
                .name("Horror")
                .build();
        when(genreService.createGenre(any())).thenReturn(createdGenre);

        // Act
        GenreDto result = genreFacade.createGenre(createDto);

        // Verify
        assertEquals("Horror", result.getName());
        verify(genreService).createGenre(argThat(g -> "Horror".equals(g.getName())));
    }

    @Test
    void createGenre_ServiceThrowsException_ThrowsException() {
        // Setup
        GenreCreateDto createDto = GenreCreateDto.builder()
                .name("Drama")
                .build();
        when(genreService.createGenre(any())).thenThrow(new RuntimeException("Service error"));

        // Act & Verify
        assertThrows(RuntimeException.class, () -> genreFacade.createGenre(createDto));
        verify(genreService).createGenre(any());
    }

    @Test
    void deleteGenre_ValidGenre_DeletesAndSendsEvent() {
        // Setup
        UUID genreId = UUID.randomUUID();
        Genre genre = Genre.builder()
                .id(genreId)
                .name("Sci-Fi")
                .build();
        when(genreService.deleteGenre(genreId)).thenReturn(genre);

        // Act
        genreFacade.deleteGenre(genreId);

        // Verify
        verify(genreService).deleteGenre(genreId);
        // Capture and verify the event sent by the producer.
        ArgumentCaptor<Object> varCaptor = ArgumentCaptor.forClass(Object.class);
        verify(genreEventProducer).sendGenreDeletedEvent(argThat(event ->
                event.getId().equals(genreId) && event.getName().equals("Sci-Fi")
        ));
    }

    @Test
    void deleteGenre_ServiceThrowsException_ThrowsException() {
        // Setup
        UUID genreId = UUID.randomUUID();
        when(genreService.deleteGenre(genreId)).thenThrow(new GenreNotFoundException(genreId));

        // Act & Verify
        assertThrows(GenreNotFoundException.class, () -> genreFacade.deleteGenre(genreId));
        verify(genreService).deleteGenre(genreId);
        verify(genreEventProducer, never()).sendGenreDeletedEvent(any());
    }
}
