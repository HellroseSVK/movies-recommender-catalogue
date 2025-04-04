package org.movierc.filmpersonservice.facade;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.movierc.filmpersonservice.dto.FilmPersonCreateDto;
import org.movierc.filmpersonservice.dto.FilmPersonDto;
import org.movierc.filmpersonservice.dto.FilmPersonUpdateDto;
import org.movierc.filmpersonservice.exception.FilmPersonNotFoundException;
import org.movierc.filmpersonservice.service.FilmPersonService;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FilmPersonFacadeTest {

    @Mock
    private FilmPersonService filmPersonService;

    @InjectMocks
    private FilmPersonFacade filmPersonFacade;

    @Test
    void getAllFilmPersons_FilmPersonsExist_ReturnsFilmPersonList() throws Exception {
        // Setup
        FilmPersonDto dto = FilmPersonDto.builder()
                .id(UUID.randomUUID())
                .name("John Doe")
                .image(new URL("http://image.png"))
                .dateOfBirth(LocalDate.of(1980, 1, 1))
                .biography("Bio")
                .build();
        when(filmPersonService.getAllFilmPersons()).thenReturn(List.of(dto));

        // Act
        List<FilmPersonDto> result = filmPersonFacade.getAllFilmPersons();

        // Verify
        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).getName());
        verify(filmPersonService).getAllFilmPersons();
    }

    @Test
    void getFilmPersonById_FilmPersonFound_ReturnsFilmPersonDto() throws Exception {
        // Setup
        UUID id = UUID.randomUUID();
        FilmPersonDto dto = FilmPersonDto.builder()
                .id(id)
                .name("Jane Doe")
                .image(new URL("http://image.png"))
                .dateOfBirth(LocalDate.of(1990, 2, 2))
                .biography("Bio Jane")
                .build();
        when(filmPersonService.getFilmPersonById(id)).thenReturn(dto);

        // Act
        FilmPersonDto result = filmPersonFacade.getFilmPersonById(id);

        // Verify
        assertEquals(id, result.getId());
        verify(filmPersonService).getFilmPersonById(id);
    }

    @Test
    void getFilmPersonById_FilmPersonNotFound_ThrowsException() {
        // Setup
        UUID id = UUID.randomUUID();
        when(filmPersonService.getFilmPersonById(id)).thenThrow(new FilmPersonNotFoundException(id));

        // Act & Verify
        assertThrows(FilmPersonNotFoundException.class, () -> filmPersonFacade.getFilmPersonById(id));
        verify(filmPersonService).getFilmPersonById(id);
    }

    @Test
    void createFilmPerson_ValidInput_ReturnsCreatedFilmPersonDto() throws Exception {
        // Setup
        FilmPersonCreateDto createDto = FilmPersonCreateDto.builder()
                .name("New Person")
                .image(new URL("http://image.png"))
                .dateOfBirth(LocalDate.of(2000, 3, 3))
                .biography("New biography")
                .build();
        FilmPersonDto returnedDto = FilmPersonDto.builder()
                .id(UUID.randomUUID())
                .name("New Person")
                .image(new URL("http://image.png"))
                .dateOfBirth(LocalDate.of(2000, 3, 3))
                .biography("New biography")
                .build();
        when(filmPersonService.createFilmPerson(createDto)).thenReturn(returnedDto);

        // Act
        FilmPersonDto result = filmPersonFacade.createFilmPerson(createDto);

        // Verify
        assertEquals("New Person", result.getName());
        verify(filmPersonService).createFilmPerson(createDto);
    }

    @Test
    void createFilmPerson_InvalidInput_ThrowsException() {
        // Setup
        FilmPersonCreateDto createDto = FilmPersonCreateDto.builder()
                .name(null) // assume null name is invalid
                .build();
        when(filmPersonService.createFilmPerson(createDto)).thenThrow(new IllegalArgumentException("Invalid input"));

        // Act & Verify
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> filmPersonFacade.createFilmPerson(createDto));
        assertEquals("Invalid input", ex.getMessage());
        verify(filmPersonService).createFilmPerson(createDto);
    }

    @Test
    void updateFilmPerson_ValidInput_ReturnsUpdatedFilmPersonDto() throws Exception {
        // Setup
        UUID id = UUID.randomUUID();
        FilmPersonUpdateDto updateDto = FilmPersonUpdateDto.builder()
                .name("Updated Name")
                .image(new URL("http://image.png"))
                .dateOfBirth(LocalDate.of(1985, 5, 5))
                .biography("Updated Bio")
                .build();
        FilmPersonDto updatedDto = FilmPersonDto.builder()
                .id(id)
                .name("Updated Name")
                .image(new URL("http://image.png"))
                .dateOfBirth(LocalDate.of(1985, 5, 5))
                .biography("Updated Bio")
                .build();
        when(filmPersonService.updateFilmPerson(id, updateDto)).thenReturn(updatedDto);

        // Act
        FilmPersonDto result = filmPersonFacade.updateFilmPerson(id, updateDto);

        // Verify
        assertEquals("Updated Name", result.getName());
        verify(filmPersonService).updateFilmPerson(id, updateDto);
    }

    @Test
    void updateFilmPerson_FilmPersonNotFound_ThrowsException() {
        // Setup
        UUID id = UUID.randomUUID();
        FilmPersonUpdateDto updateDto = FilmPersonUpdateDto.builder()
                .name("Non existent")
                .build();
        when(filmPersonService.updateFilmPerson(id, updateDto))
                .thenThrow(new FilmPersonNotFoundException(id));

        // Act & Verify
        assertThrows(FilmPersonNotFoundException.class, () -> filmPersonFacade.updateFilmPerson(id, updateDto));
        verify(filmPersonService).updateFilmPerson(id, updateDto);
    }

    @Test
    void deleteFilmPerson_ValidId_DeletesFilmPerson() {
        // Setup
        UUID id = UUID.randomUUID();
        // No behavior needed for a void method when successful.

        // Act
        filmPersonFacade.deleteFilmPerson(id);

        // Verify
        verify(filmPersonService).deleteFilmPerson(id);
    }

    @Test
    void deleteFilmPerson_FilmPersonNotFound_ThrowsException() {
        // Setup
        UUID id = UUID.randomUUID();
        doThrow(new FilmPersonNotFoundException(id)).when(filmPersonService).deleteFilmPerson(id);

        // Act & Verify
        assertThrows(FilmPersonNotFoundException.class, () -> filmPersonFacade.deleteFilmPerson(id));
        verify(filmPersonService).deleteFilmPerson(id);
    }
}
