package org.movierc.userservice.rabbit;

import lombok.AllArgsConstructor;
import org.movierc.events.EventUtil;
import org.movierc.events.user.UserCreatedEvent;
import org.movierc.events.user.UserDeletedEvent;
import org.movierc.events.user.UserEvents;
import org.movierc.events.user.UserUpdatedEvent;
import org.movierc.userservice.configuration.RabbitConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserEventProducer {
    private final RabbitTemplate rabbitTemplate;

    public void sendUserCreatedEvent(UserCreatedEvent event) {
        rabbitTemplate.convertAndSend(EventUtil.forService(UserEvents.USER_CREATED_QUEUE, RabbitConfig.SERVICE_ID), event);
    }

    public void sendUserUpdatedEvent(UserUpdatedEvent event) {
        rabbitTemplate.convertAndSend(EventUtil.forService(UserEvents.USER_UPDATED_QUEUE, RabbitConfig.SERVICE_ID), event);
    }

    public void sendUserDeletedEvent(UserDeletedEvent event) {
        rabbitTemplate.convertAndSend(EventUtil.forService(UserEvents.USER_DELETED_QUEUE, RabbitConfig.SERVICE_ID), event);
    }
}
