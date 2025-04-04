package org.movierc.userservice.rabbit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.movierc.events.user.UserCreatedEvent;
import org.movierc.events.user.UserDeletedEvent;
import org.movierc.events.user.UserEvents;
import org.movierc.events.user.UserUpdatedEvent;
import org.movierc.userservice.configuration.RabbitConfig;
import org.movierc.events.EventUtil;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserEventProducerTest {

    @InjectMocks
    private UserEventProducer producer;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Test
    public void sendUserCreatedEvent_ValidEvent_SendsEventSuccessfully() {
        // Setup
        UserCreatedEvent event = UserCreatedEvent.builder()
                .id(UUID.randomUUID())
                .username("newUser")
                .email("newUser@example.com")
                .build();
        String expectedQueue = EventUtil.forService(UserEvents.USER_CREATED_QUEUE, RabbitConfig.SERVICE_ID);

        // Act
        producer.sendUserCreatedEvent(event);

        // Verify
        verify(rabbitTemplate, times(1)).convertAndSend(expectedQueue, event);
    }

    @Test
    public void sendUserCreatedEvent_RabbitTemplateThrowsException_ExceptionPropagates() {
        // Setup
        UserCreatedEvent event = UserCreatedEvent.builder()
                .id(UUID.randomUUID())
                .username("newUser")
                .email("newUser@example.com")
                .build();
        String expectedQueue = EventUtil.forService(UserEvents.USER_CREATED_QUEUE, RabbitConfig.SERVICE_ID);
        RuntimeException exception = new RuntimeException("Send error");
        doThrow(exception).when(rabbitTemplate).convertAndSend(expectedQueue, event);

        // Act & Verify
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> producer.sendUserCreatedEvent(event));
        assertEquals("Send error", thrown.getMessage());
        verify(rabbitTemplate, times(1)).convertAndSend(expectedQueue, event);
    }

    @Test
    public void sendUserUpdatedEvent_ValidEvent_SendsEventSuccessfully() {
        // Setup
        UserUpdatedEvent event = UserUpdatedEvent.builder()
                .id(UUID.randomUUID())
                .username("updatedUser")
                .email("updatedUser@example.com")
                .build();
        String expectedQueue = EventUtil.forService(UserEvents.USER_UPDATED_QUEUE, RabbitConfig.SERVICE_ID);

        // Act
        producer.sendUserUpdatedEvent(event);

        // Verify
        verify(rabbitTemplate, times(1)).convertAndSend(expectedQueue, event);
    }

    @Test
    public void sendUserUpdatedEvent_RabbitTemplateThrowsException_ExceptionPropagates() {
        // Setup
        UserUpdatedEvent event = UserUpdatedEvent.builder()
                .id(UUID.randomUUID())
                .username("updatedUser")
                .email("updatedUser@example.com")
                .build();
        String expectedQueue = EventUtil.forService(UserEvents.USER_UPDATED_QUEUE, RabbitConfig.SERVICE_ID);
        RuntimeException exception = new RuntimeException("Send error");
        doThrow(exception).when(rabbitTemplate).convertAndSend(expectedQueue, event);

        // Act & Verify
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> producer.sendUserUpdatedEvent(event));
        assertEquals("Send error", thrown.getMessage());
        verify(rabbitTemplate, times(1)).convertAndSend(expectedQueue, event);
    }

    @Test
    public void sendUserDeletedEvent_ValidEvent_SendsEventSuccessfully() {
        // Setup
        UserDeletedEvent event = UserDeletedEvent.builder()
                .id(UUID.randomUUID())
                .username("deletedUser")
                .build();
        String expectedQueue = EventUtil.forService(UserEvents.USER_DELETED_QUEUE, RabbitConfig.SERVICE_ID);

        // Act
        producer.sendUserDeletedEvent(event);

        // Verify
        verify(rabbitTemplate, times(1)).convertAndSend(expectedQueue, event);
    }

    @Test
    public void sendUserDeletedEvent_RabbitTemplateThrowsException_ExceptionPropagates() {
        // Setup
        UserDeletedEvent event = UserDeletedEvent.builder()
                .id(UUID.randomUUID())
                .username("deletedUser")
                .build();
        String expectedQueue = EventUtil.forService(UserEvents.USER_DELETED_QUEUE, RabbitConfig.SERVICE_ID);
        RuntimeException exception = new RuntimeException("Send error");
        doThrow(exception).when(rabbitTemplate).convertAndSend(expectedQueue, event);

        // Act & Verify
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> producer.sendUserDeletedEvent(event));
        assertEquals("Send error", thrown.getMessage());
        verify(rabbitTemplate, times(1)).convertAndSend(expectedQueue, event);
    }
}