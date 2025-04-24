package org.movierc.movieservice.rabbit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.movierc.events.filmPerson.FilmPersonCreatedEvent;
import org.movierc.events.filmPerson.FilmPersonUpdatedEvent;
import org.movierc.events.filmPerson.FilmPersonDeletedEvent;
import org.movierc.movieservice.entity.FilmPerson;
import org.movierc.movieservice.mapper.FilmPersonCreatedEventMapper;
import org.movierc.movieservice.repository.FilmPersonRepository;

import java.net.URL;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FilmPersonEventConsumerTest {

    @Mock
    private FilmPersonRepository filmPersonRepository;

    private final FilmPersonCreatedEventMapper filmPersonCreatedEventMapper =
            Mappers.getMapper(FilmPersonCreatedEventMapper.class);

    private FilmPersonEventConsumer filmPersonEventConsumer;

    @BeforeEach
    void setUp() {
        filmPersonEventConsumer = new FilmPersonEventConsumer(filmPersonCreatedEventMapper, filmPersonRepository);
    }

    @Test
    void handleFilmPersonCreatedEvent_NewEvent_SavesEvent() {
        // Setup
        UUID eventId = UUID.randomUUID();
        FilmPersonCreatedEvent event = new FilmPersonCreatedEvent();
        event.setId(eventId);
        when(filmPersonRepository.findById(eventId)).thenReturn(Optional.empty());
        // Act
        filmPersonEventConsumer.handleFilmPersonCreatedEvent(event);
        // Verify
        verify(filmPersonRepository, times(1)).save(any());
    }

    @Test
    void handleFilmPersonCreatedEvent_ExistingEvent_DoesNotSave() {
        // Setup
        UUID eventId = UUID.randomUUID();
        FilmPersonCreatedEvent event = new FilmPersonCreatedEvent();
        event.setId(eventId);
        // Simulate already existing entity (details not necessary)
        when(filmPersonRepository.findById(eventId)).thenReturn(Optional.of(new FilmPerson()));
        // Act
        filmPersonEventConsumer.handleFilmPersonCreatedEvent(event);
        // Verify
        verify(filmPersonRepository, never()).save(any());
    }

    @Test
    void handleFilmPersonCreatedEvent_RepositoryException_ThrowsException() {
        // Setup
        UUID eventId = UUID.randomUUID();
        FilmPersonCreatedEvent event = new FilmPersonCreatedEvent();
        event.setId(eventId);
        when(filmPersonRepository.findById(eventId)).thenReturn(Optional.empty());
        doThrow(new RuntimeException("DB error")).when(filmPersonRepository).save(any());
        // Act & Verify
        assertThrows(RuntimeException.class, () -> filmPersonEventConsumer.handleFilmPersonCreatedEvent(event));
    }

    @Test
    void handleFilmPersonUpdatedEvent_ExistingEvent_UpdatesAndSaves() throws Exception {
        // Setup
        UUID eventId = UUID.randomUUID();
        FilmPersonUpdatedEvent updatedEvent = new FilmPersonUpdatedEvent();
        updatedEvent.setId(eventId);
        updatedEvent.setName("Jane Doe");
        updatedEvent.setImage(new URL("http://example.com/new-image"));
        updatedEvent.setDateOfBirth(LocalDate.of(1990, 1, 1));
        updatedEvent.setBiography("Updated biography");

        // Create a dummy FilmPerson with initial values
        FilmPerson existingFilmPerson = new FilmPerson();
        existingFilmPerson.setId(eventId);
        existingFilmPerson.setName("John Doe");
        existingFilmPerson.setImage(new URL("http://example.com/old-image"));
        existingFilmPerson.setDateOfBirth(LocalDate.of(1980, 1, 1));
        existingFilmPerson.setBiography("Old biography");

        when(filmPersonRepository.findById(eventId)).thenReturn(Optional.of(existingFilmPerson));

        // Act
        filmPersonEventConsumer.handleFilmPersonUpdatedEvent(updatedEvent);

        // Verify that the repository was called to save the updated entity
        verify(filmPersonRepository).save(existingFilmPerson);
        // Optionally verify that fields were updated
        assertEquals("Jane Doe", existingFilmPerson.getName());
        assertEquals(new URL("http://example.com/new-image"), existingFilmPerson.getImage());
        assertEquals(LocalDate.of(1990, 1, 1), existingFilmPerson.getDateOfBirth());
        assertEquals("Updated biography", existingFilmPerson.getBiography());
    }

    @Test
    void handleFilmPersonUpdatedEvent_NonExistingEvent_DoesNotSave() {
        // Setup
        UUID eventId = UUID.randomUUID();
        FilmPersonUpdatedEvent updatedEvent = new FilmPersonUpdatedEvent();
        updatedEvent.setId(eventId);
        updatedEvent.setName("Jane Doe");
        // ...other fields...

        when(filmPersonRepository.findById(eventId)).thenReturn(Optional.empty());

        // Act
        filmPersonEventConsumer.handleFilmPersonUpdatedEvent(updatedEvent);

        // Verify that save is never called
        verify(filmPersonRepository, never()).save(any());
    }

    @Test
    void handleFilmPersonDeletedEvent_CallsDelete() {
        // Setup
        UUID eventId = UUID.randomUUID();
        FilmPersonDeletedEvent deletedEvent = new FilmPersonDeletedEvent();
        deletedEvent.setId(eventId);

        // Act
        filmPersonEventConsumer.handleFilmPersonDeletedEvent(deletedEvent);

        // Verify deletion was performed
        verify(filmPersonRepository).deleteById(eventId);
    }
}
