package org.movierc.movieservice.facade;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.movierc.movieservice.dto.GenreCreateDto;
import org.movierc.movieservice.dto.GenreDto;
import org.movierc.movieservice.service.GenreService;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class GenreFacadeTest {

    @Mock
    private GenreService genreService;

    @InjectMocks
    private GenreFacade genreFacade;

    @Test
    void getGenres_GenresExist_ReturnsList() {
        GenreDto dto = GenreDto.builder()
                .id(UUID.randomUUID())
                .name("Comedy")
                .build();
        when(genreService.getGenres()).thenReturn(List.of(dto));

        List<GenreDto> result = genreFacade.getGenres();

        assertEquals(1, result.size());
        assertEquals("Comedy", result.get(0).getName());
        verify(genreService).getGenres();
    }

    @Test
    void createGenre_ValidInput_ReturnsCreatedGenreDto() {
        GenreCreateDto createDto = GenreCreateDto.builder()
                .name("Horror")
                .build();
        GenreDto createdDto = GenreDto.builder()
                .id(UUID.randomUUID())
                .name("Horror")
                .build();
        when(genreService.createGenre(createDto)).thenReturn(createdDto);

        GenreDto result = genreFacade.createGenre(createDto);

        assertEquals("Horror", result.getName());
        verify(genreService).createGenre(createDto);
    }

    @Test
    void deleteGenre_ValidId_DeletesGenre() {
        UUID id = UUID.randomUUID();
        genreFacade.deleteGenre(id);
        verify(genreService).deleteGenre(id);
    }

    @Test
    void getGenres_ServiceThrowsException_PropagatesException() {
        when(genreService.getGenres()).thenThrow(new RuntimeException("Error"));
        assertThrows(RuntimeException.class, () -> genreFacade.getGenres());
    }

    @Test
    void createGenre_ServiceThrowsException_PropagatesException() {
        GenreCreateDto createDto = GenreCreateDto.builder()
                .name("Invalid")
                .build();
        when(genreService.createGenre(createDto)).thenThrow(new RuntimeException("Error creating genre"));
        
        assertThrows(RuntimeException.class, () -> genreFacade.createGenre(createDto));
        verify(genreService).createGenre(createDto);
    }

    @Test
    void deleteGenre_ServiceThrowsException_PropagatesException() {
        UUID id = UUID.randomUUID();
        // Simulate exception in service layer for deleteGenre
        doThrow(new RuntimeException("Error deleting genre")).when(genreService).deleteGenre(id);
        
        assertThrows(RuntimeException.class, () -> genreFacade.deleteGenre(id));
        verify(genreService).deleteGenre(id);
    }
}
