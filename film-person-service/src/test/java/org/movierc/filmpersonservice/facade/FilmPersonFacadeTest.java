package org.movierc.filmpersonservice.facade;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.movierc.filmpersonservice.dto.FilmPersonCreateDto;
import org.movierc.filmpersonservice.dto.FilmPersonDto;
import org.movierc.filmpersonservice.dto.FilmPersonUpdateDto;
import org.movierc.filmpersonservice.entity.FilmPerson;
import org.movierc.filmpersonservice.exception.FilmPersonNotFoundException;
import org.movierc.filmpersonservice.mapper.FilmPersonCreateDtoMapper;
import org.movierc.filmpersonservice.mapper.FilmPersonMapper;
import org.movierc.filmpersonservice.rabbit.FilmPersonEventProducer;
import org.movierc.filmpersonservice.service.FilmPersonService;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FilmPersonFacadeTest {

    @Mock
    private FilmPersonService filmPersonService;

    @Mock
    private FilmPersonEventProducer filmPersonEventProducer;

    @Mock
    private FilmPersonCreateDtoMapper filmPersonCreateDtoMapper;

    @Mock
    private FilmPersonMapper filmPersonMapper;

    @InjectMocks
    private FilmPersonFacade filmPersonFacade;

    @Test
    void getAllFilmPersons_ReturnsFilmPersonDtoList() throws Exception {
        FilmPerson filmPerson = FilmPerson.builder()
            .id(UUID.randomUUID())
            .name("John Doe")
            .image(new URL("http://image.png"))
            .dateOfBirth(LocalDate.of(1980, 1, 1))
            .biography("Bio")
            .build();
        when(filmPersonService.getAllFilmPersons()).thenReturn(List.of(filmPerson));
        when(filmPersonMapper.filmPersonToFilmPersonDto(filmPerson)).thenReturn(FilmPersonDto.builder()
            .id(filmPerson.getId())
            .name(filmPerson.getName())
            .image(filmPerson.getImage())
            .dateOfBirth(filmPerson.getDateOfBirth())
            .biography(filmPerson.getBiography())
            .build());

        List<FilmPersonDto> result = filmPersonFacade.getAllFilmPersons();

        assertEquals(1, result.size());
        assertEquals("John Doe", result.getFirst().getName());
        verify(filmPersonService).getAllFilmPersons();
    }

    @Test
    void getFilmPersonById_FilmPersonFound_ReturnsFilmPersonDto() throws Exception {
        UUID id = UUID.randomUUID();
        FilmPerson filmPerson = FilmPerson.builder()
            .id(id)
            .name("Jane Doe")
            .image(new URL("http://image.png"))
            .dateOfBirth(LocalDate.of(1990, 2, 2))
            .biography("Bio Jane")
            .build();
        FilmPersonDto filmPersonDto = FilmPersonDto.builder()
            .id(id)
            .name("Jane Doe")
            .image(new URL("http://image.png"))
            .dateOfBirth(LocalDate.of(1990, 2, 2))
            .biography("Bio Jane")
            .build();

        when(filmPersonService.getFilmPersonById(id)).thenReturn(filmPerson);
        when(filmPersonMapper.filmPersonToFilmPersonDto(filmPerson)).thenReturn(filmPersonDto);

        FilmPersonDto result = filmPersonFacade.getFilmPersonById(id);

        assertEquals(id, result.getId());
        verify(filmPersonService).getFilmPersonById(id);
        verify(filmPersonMapper).filmPersonToFilmPersonDto(filmPerson);
    }

    @Test
    void getFilmPersonById_FilmPersonNotFound_ThrowsException() {
        UUID id = UUID.randomUUID();
        when(filmPersonService.getFilmPersonById(id)).thenThrow(new FilmPersonNotFoundException(id));

        assertThrows(FilmPersonNotFoundException.class, () -> filmPersonFacade.getFilmPersonById(id));
        verify(filmPersonService).getFilmPersonById(id);
    }

    @Test
    void createFilmPerson_ValidInput_ReturnsCreatedFilmPersonDto() throws Exception {
        FilmPersonCreateDto createDto = FilmPersonCreateDto.builder()
            .name("New Person")
            .image(new URL("http://image.png"))
            .dateOfBirth(LocalDate.of(2000, 3, 3))
            .biography("New biography")
            .build();
        FilmPerson filmPerson = FilmPerson.builder()
            .id(UUID.randomUUID())
            .name("New Person")
            .image(new URL("http://image.png"))
            .dateOfBirth(LocalDate.of(2000, 3, 3))
            .biography("New biography")
            .build();
        FilmPersonDto filmPersonDto = FilmPersonDto.builder()
            .id(filmPerson.getId())
            .name(filmPerson.getName())
            .image(filmPerson.getImage())
            .dateOfBirth(filmPerson.getDateOfBirth())
            .biography(filmPerson.getBiography())
            .build();

        when(filmPersonCreateDtoMapper.filmPersonCreateDtoToMovie(createDto)).thenReturn(filmPerson);
        when(filmPersonService.createFilmPerson(filmPerson)).thenReturn(filmPerson);
        when(filmPersonMapper.filmPersonToFilmPersonDto(filmPerson)).thenReturn(filmPersonDto);

        FilmPersonDto result = filmPersonFacade.createFilmPerson(createDto);

        assertEquals("New Person", result.getName());
        verify(filmPersonCreateDtoMapper).filmPersonCreateDtoToMovie(createDto);
        verify(filmPersonService).createFilmPerson(filmPerson);
        verify(filmPersonMapper).filmPersonToFilmPersonDto(filmPerson);
    }

    @Test
    void updateFilmPerson_ValidInput_ReturnsUpdatedFilmPersonDto() throws Exception {
        UUID id = UUID.randomUUID();
        FilmPersonUpdateDto updateDto = FilmPersonUpdateDto.builder()
            .name("Updated Name")
            .image(new URL("http://image.png"))
            .dateOfBirth(LocalDate.of(1985, 5, 5))
            .biography("Updated Bio")
            .build();
        FilmPerson updatedFilmPerson = FilmPerson.builder()
            .id(id)
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

        when(filmPersonService.updateFilmPerson(id, updateDto)).thenReturn(updatedFilmPerson);
        when(filmPersonMapper.filmPersonToFilmPersonDto(updatedFilmPerson)).thenReturn(updatedDto);

        FilmPersonDto result = filmPersonFacade.updateFilmPerson(id, updateDto);

        assertEquals("Updated Name", result.getName());
        verify(filmPersonService).updateFilmPerson(id, updateDto);
        verify(filmPersonMapper).filmPersonToFilmPersonDto(updatedFilmPerson);
    }

    @Test
    void deleteFilmPerson_ValidId_DeletesFilmPerson() {
        UUID id = UUID.randomUUID();
        FilmPerson deletedFilmPerson = FilmPerson.builder()
            .id(id)
            .name("Deleted Person")
            .build();
        when(filmPersonService.deleteFilmPerson(id)).thenReturn(deletedFilmPerson);

        filmPersonFacade.deleteFilmPerson(id);

        verify(filmPersonService).deleteFilmPerson(id);
        verify(filmPersonMapper).filmPersonToFilmPersonDeletedEvent(deletedFilmPerson);
    }

    @Test
    void deleteFilmPerson_FilmPersonNotFound_ThrowsException() {
        UUID id = UUID.randomUUID();
        doThrow(new FilmPersonNotFoundException(id)).when(filmPersonService).deleteFilmPerson(id);

        assertThrows(FilmPersonNotFoundException.class, () -> filmPersonFacade.deleteFilmPerson(id));
        verify(filmPersonService).deleteFilmPerson(id);
    }

    @Test
    void createFilmPerson_ProducesCreatedEvent() throws Exception {
        // Setup
        FilmPersonCreateDto createDto = FilmPersonCreateDto.builder()
            .name("New Person")
            .image(new URL("http://image.png"))
            .dateOfBirth(LocalDate.of(2000, 3, 3))
            .biography("New biography")
            .build();
        FilmPerson filmPerson = FilmPerson.builder()
            .id(UUID.randomUUID())
            .name("New Person")
            .image(new URL("http://image.png"))
            .dateOfBirth(LocalDate.of(2000, 3, 3))
            .biography("New biography")
            .build();
        FilmPersonDto filmPersonDto = FilmPersonDto.builder()
            .id(filmPerson.getId())
            .name(filmPerson.getName())
            .image(filmPerson.getImage())
            .dateOfBirth(filmPerson.getDateOfBirth())
            .biography(filmPerson.getBiography())
            .build();

        when(filmPersonCreateDtoMapper.filmPersonCreateDtoToMovie(createDto)).thenReturn(filmPerson);
        when(filmPersonService.createFilmPerson(filmPerson)).thenReturn(filmPerson);
        when(filmPersonMapper.filmPersonToFilmPersonDto(filmPerson)).thenReturn(filmPersonDto);

        // Act
        FilmPersonDto result = filmPersonFacade.createFilmPerson(createDto);

        // Verify
        assertEquals("New Person", result.getName());
        verify(filmPersonService).createFilmPerson(filmPerson);
        verify(filmPersonEventProducer).sendFilmPersonCreatedEvent(any());
    }

    @Test
    void updateFilmPerson_ProducesUpdatedEvent() throws Exception {
        // Setup
        UUID id = UUID.randomUUID();
        FilmPersonUpdateDto updateDto = FilmPersonUpdateDto.builder()
            .name("Updated Name")
            .image(new URL("http://image.png"))
            .dateOfBirth(LocalDate.of(1985, 5, 5))
            .biography("Updated Bio")
            .build();
        FilmPerson updatedFilmPerson = FilmPerson.builder()
            .id(id)
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

        when(filmPersonService.updateFilmPerson(id, updateDto)).thenReturn(updatedFilmPerson);
        when(filmPersonMapper.filmPersonToFilmPersonDto(updatedFilmPerson)).thenReturn(updatedDto);

        // Act
        FilmPersonDto result = filmPersonFacade.updateFilmPerson(id, updateDto);

        // Verify
        assertEquals("Updated Name", result.getName());
        verify(filmPersonService).updateFilmPerson(id, updateDto);
        verify(filmPersonEventProducer).sendFilmPersonUpdatedEvent(any());
    }

    @Test
    void deleteFilmPerson_ProducesDeletedEvent() {
        // Setup
        UUID id = UUID.randomUUID();
        FilmPerson deletedFilmPerson = FilmPerson.builder()
            .id(id)
            .name("Deleted Person")
            .build();

        when(filmPersonService.deleteFilmPerson(id)).thenReturn(deletedFilmPerson);

        // Act
        filmPersonFacade.deleteFilmPerson(id);

        // Verify
        verify(filmPersonService).deleteFilmPerson(id);
        verify(filmPersonEventProducer).sendFilmPersonDeletedEvent(any());
    }
}