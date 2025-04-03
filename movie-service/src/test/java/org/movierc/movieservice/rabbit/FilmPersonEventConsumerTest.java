package org.movierc.movieservice.rabbit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.movierc.events.filmPerson.FilmPersonCreatedEvent;
import org.movierc.movieservice.entity.FilmPerson;
import org.movierc.movieservice.mapper.FilmPersonCreatedEventMapper;
import org.movierc.movieservice.repository.FilmPersonRepository;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
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
        assertThrows(RuntimeException.class, () -> {
            filmPersonEventConsumer.handleFilmPersonCreatedEvent(event);
        });
    }
}
