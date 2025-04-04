package org.movierc.filmpersonservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.movierc.events.filmPerson.FilmPersonCreatedEvent;
import org.movierc.events.filmPerson.FilmPersonDeletedEvent;
import org.movierc.events.filmPerson.FilmPersonUpdatedEvent;
import org.movierc.filmpersonservice.dto.FilmPersonCreateDto;
import org.movierc.filmpersonservice.dto.FilmPersonDto;
import org.movierc.filmpersonservice.dto.FilmPersonUpdateDto;
import org.movierc.filmpersonservice.entity.FilmPerson;
import org.movierc.filmpersonservice.exception.FilmPersonNotFoundException;
import org.movierc.filmpersonservice.mapper.FilmPersonCreateDtoMapper;
import org.movierc.filmpersonservice.mapper.FilmPersonMapper;
import org.movierc.filmpersonservice.rabbit.FilmPersonEventProducer;
import org.movierc.filmpersonservice.repository.FilmPersonRepository;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FilmPersonServiceTest {

    @Mock
    private FilmPersonRepository filmPersonRepository;

    @Mock
    private FilmPersonEventProducer filmPersonEventProducer;

    private final FilmPersonMapper filmPersonMapper = Mappers.getMapper(FilmPersonMapper.class);

    private final FilmPersonCreateDtoMapper filmPersonCreateDtoMapper = Mappers.getMapper(FilmPersonCreateDtoMapper.class);

    private FilmPersonService filmPersonService;

    @BeforeEach
    void setUp() {
        filmPersonService = new FilmPersonService(filmPersonRepository, filmPersonEventProducer, filmPersonMapper, filmPersonCreateDtoMapper);
    }

    @Test
    void getAllFilmPersons_ReturnsListOfDtos() throws Exception {
        // Setup
        FilmPerson fp = FilmPerson.builder()
                .id(UUID.randomUUID())
                .name("John Doe")
                .image(new URL("http://image.png"))
                .dateOfBirth(LocalDate.of(1980, 1, 1))
                .biography("Bio")
                .movies(new ArrayList<>())
                .build();
        when(filmPersonRepository.findAll()).thenReturn(List.of(fp));

        // Act
        List<FilmPersonDto> result = filmPersonService.getAllFilmPersons();

        // Verify
        assertEquals(1, result.size());
        assertEquals(fp.getId(), result.getFirst().getId());
    }

    @Test
    void getFilmPersonById_FilmPersonExists_ReturnsDto() throws Exception {
        // Setup
        UUID id = UUID.randomUUID();
        FilmPerson fp = FilmPerson.builder()
                .id(id)
                .name("Jane Doe")
                .image(new URL("http://image.png"))
                .dateOfBirth(LocalDate.of(1990, 2, 2))
                .biography("Jane's bio")
                .movies(new ArrayList<>())
                .build();
        when(filmPersonRepository.findById(id)).thenReturn(Optional.of(fp));

        // Act
        FilmPersonDto result = filmPersonService.getFilmPersonById(id);

        // Verify
        assertNotNull(result);
        assertEquals(fp.getId(), result.getId());
        assertEquals(fp.getName(), result.getName());
    }

    @Test
    void getFilmPersonById_FilmPersonDoesNotExist_ThrowsException() {
        // Setup
        UUID id = UUID.randomUUID();
        when(filmPersonRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Verify
        assertThrows(FilmPersonNotFoundException.class, () -> filmPersonService.getFilmPersonById(id));
    }

    @Test
    void createFilmPerson_ValidDto_ReturnsCreatedDtoAndSendsEvent() throws Exception {
        // Setup
        FilmPersonCreateDto createDto = FilmPersonCreateDto.builder()
                .name("Alice")
                .image(new URL("http://image.png"))
                .dateOfBirth(LocalDate.of(1985, 3, 3))
                .biography("Alice's bio")
                .build();
        // When save is called, return the same film person argument.
        when(filmPersonRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        FilmPersonDto result = filmPersonService.createFilmPerson(createDto);

        // Verify repository and event producer
        verify(filmPersonRepository).save(any());
        assertNotNull(result.getId());
        assertEquals(createDto.getName(), result.getName());

        var createdEventCaptor = ArgumentCaptor.forClass(FilmPersonCreatedEvent.class);
        verify(filmPersonEventProducer).sendFilmPersonCreatedEvent(createdEventCaptor.capture());
        assertEquals(createdEventCaptor.getValue().getId(), result.getId());
        assertEquals(createdEventCaptor.getValue().getName(), result.getName());
        assertEquals(createdEventCaptor.getValue().getImage(), result.getImage());
        assertEquals(createdEventCaptor.getValue().getBiography(), result.getBiography());
        assertEquals(createdEventCaptor.getValue().getDateOfBirth(), result.getDateOfBirth());
    }

    @Test
    void updateFilmPerson_FilmPersonExists_UpdatesAndReturnsDtoAndSendsEvent() throws Exception {
        // Setup
        UUID id = UUID.randomUUID();
        FilmPerson fp = FilmPerson.builder()
                .id(id)
                .name("Old Name")
                .image(new URL("http://image.png"))
                .dateOfBirth(LocalDate.of(1970, 1, 1))
                .biography("Old bio")
                .movies(new ArrayList<>())
                .build();
        when(filmPersonRepository.findById(id)).thenReturn(Optional.of(fp));
        when(filmPersonRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        FilmPersonUpdateDto updateDto = FilmPersonUpdateDto.builder()
                .name("New Name")
                .image(new URL("http://image.png"))
                .dateOfBirth(LocalDate.of(1980, 2, 2))
                .biography("New bio")
                .build();

        // Act
        FilmPersonDto result = filmPersonService.updateFilmPerson(id, updateDto);

        // Verify updates
        assertEquals(updateDto.getName(), result.getName());
        assertEquals(updateDto.getImage(), result.getImage());
        assertEquals(updateDto.getBiography(), result.getBiography());
        assertEquals(updateDto.getDateOfBirth(), result.getDateOfBirth());

        // Verify event producer call
        var updatedEventCaptor = ArgumentCaptor.forClass(FilmPersonUpdatedEvent.class);
        verify(filmPersonEventProducer).sendFilmPersonUpdatedEvent(updatedEventCaptor.capture());
        assertEquals(updatedEventCaptor.getValue().getId(), id);
        assertEquals(updatedEventCaptor.getValue().getName(), updateDto.getName());
        assertEquals(updatedEventCaptor.getValue().getImage(), updateDto.getImage());
        assertEquals(updatedEventCaptor.getValue().getBiography(), updateDto.getBiography());
    }

    @Test
    void updateFilmPerson_FilmPersonDoesNotExist_ThrowsException() throws Exception {
        // Setup
        UUID id = UUID.randomUUID();
        when(filmPersonRepository.findById(id)).thenReturn(Optional.empty());
        FilmPersonUpdateDto updateDto = FilmPersonUpdateDto.builder()
                .name("Name")
                .image(new URL("http://image.png"))
                .dateOfBirth(LocalDate.now())
                .biography("bio")
                .build();

        // Act & Verify
        assertThrows(FilmPersonNotFoundException.class, () -> filmPersonService.updateFilmPerson(id, updateDto));
    }

    @Test
    void deleteFilmPerson_FilmPersonExists_DeletesAndSendsEvent() throws Exception {
        // Setup
        UUID id = UUID.randomUUID();
        FilmPerson fp = FilmPerson.builder()
                .id(id)
                .name("Bob")
                .image(new URL("http://image.png"))
                .dateOfBirth(LocalDate.of(1965, 4, 4))
                .biography("Bob's bio")
                .movies(new ArrayList<>())
                .build();
        when(filmPersonRepository.findById(id)).thenReturn(Optional.of(fp));

        // Act
        filmPersonService.deleteFilmPerson(id);

        // Verify deletion and event producer call
        verify(filmPersonRepository).deleteById(id);

        var deletedEventCaptor = ArgumentCaptor.forClass(FilmPersonDeletedEvent.class);
        verify(filmPersonEventProducer).sendFilmPersonDeletedEvent(deletedEventCaptor.capture());
        assertEquals(deletedEventCaptor.getValue().getId(), id);
        assertEquals(deletedEventCaptor.getValue().getName(), fp.getName());
    }

    @Test
    void deleteFilmPerson_FilmPersonDoesNotExist_ThrowsException() {
        // Setup
        UUID id = UUID.randomUUID();
        when(filmPersonRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Verify
        assertThrows(FilmPersonNotFoundException.class, () -> filmPersonService.deleteFilmPerson(id));
    }
}
