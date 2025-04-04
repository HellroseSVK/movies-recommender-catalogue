package org.movierc.feedbackservice.rabbit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.movierc.events.user.UserCreatedEvent;
import org.movierc.events.user.UserUpdatedEvent;
import org.movierc.events.user.UserDeletedEvent;
import org.movierc.feedbackservice.entity.User;
import org.movierc.feedbackservice.mapper.UserEventMapper;
import org.movierc.feedbackservice.repository.UserRepository;

import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserEventConsumerTest {

    @Mock
    private UserRepository userRepository;

    private final UserEventMapper userEventMapper = Mappers.getMapper(UserEventMapper.class);

    private UserEventConsumer userEventConsumer;

    @BeforeEach
    void setUp() {
        userEventConsumer = new UserEventConsumer(userRepository, userEventMapper);
    }

    @Test
    void handleUserCreatedEvent_WhenUserNotExists_SavesUser() {
        // Setup
        UUID userId = UUID.randomUUID();
        UserCreatedEvent event = new UserCreatedEvent();
        event.setId(userId);
        event.setUsername("new_user");

        when(userRepository.existsById(userId)).thenReturn(false);

        // Act
        userEventConsumer.handleUserCreatedEvent(event);

        // Verify
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void handleUserCreatedEvent_WhenUserExists_DoesNotSave() {
        // Setup
        UUID userId = UUID.randomUUID();
        UserCreatedEvent event = new UserCreatedEvent();
        event.setId(userId);
        event.setUsername("existing_user");

        when(userRepository.existsById(userId)).thenReturn(true);

        // Act
        userEventConsumer.handleUserCreatedEvent(event);

        // Verify
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void handleUserUpdatedEvent_WhenUserExists_UpdatesUser() {
        // Setup
        UUID userId = UUID.randomUUID();
        UserUpdatedEvent event = new UserUpdatedEvent();
        event.setId(userId);
        event.setUsername("updated_user");

        when(userRepository.existsById(userId)).thenReturn(true);

        // Act
        userEventConsumer.handleUserUpdatedEvent(event);

        // Verify
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void handleUserUpdatedEvent_WhenUserNotExists_DoesNotUpdate() {
        // Setup
        UUID userId = UUID.randomUUID();
        UserUpdatedEvent event = new UserUpdatedEvent();
        event.setId(userId);
        event.setUsername("ghost_user");

        when(userRepository.existsById(userId)).thenReturn(false);

        // Act
        userEventConsumer.handleUserUpdatedEvent(event);

        // Verify
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void handleUserDeletedEvent_WhenUserExists_DeletesUser() {
        // Setup
        UUID userId = UUID.randomUUID();
        UserDeletedEvent event = new UserDeletedEvent();
        event.setId(userId);

        when(userRepository.existsById(userId)).thenReturn(true);

        // Act
        userEventConsumer.handleUserDeletedEvent(event);

        // Verify
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void handleUserDeletedEvent_WhenUserNotExists_DoesNotDelete() {
        // Setup
        UUID userId = UUID.randomUUID();
        UserDeletedEvent event = new UserDeletedEvent();
        event.setId(userId);

        when(userRepository.existsById(userId)).thenReturn(false);

        // Act
        userEventConsumer.handleUserDeletedEvent(event);

        // Verify
        verify(userRepository, never()).deleteById(any());
    }
}
