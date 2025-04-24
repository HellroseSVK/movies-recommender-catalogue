package org.movierc.feedbackservice.rabbit;

import lombok.AllArgsConstructor;
import org.movierc.feedbackservice.mapper.UserEventMapper;
import org.movierc.feedbackservice.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@AllArgsConstructor
public class UserEventConsumer {
    private final Logger logger = LoggerFactory.getLogger(UserEventConsumer.class);

    private final UserRepository userRepository;

    private final UserEventMapper userEventMapper;

    @RabbitListener(queues = "#{userCreatedQueue.getName()}")
    @Transactional
    public void handleUserCreatedEvent(org.movierc.events.user.UserCreatedEvent event) {
        if (!userRepository.existsById(event.getId())) {
            userRepository.save(userEventMapper.userCreatedEventToUser(event));
        }

        logger.info("Received User Created: {}", event);
    }

    @RabbitListener(queues = "#{userUpdatedQueue.getName()}")
    @Transactional
    public void handleUserUpdatedEvent(org.movierc.events.user.UserUpdatedEvent event) {
        if (userRepository.existsById(event.getId())) {
            userRepository.save(userEventMapper.userUpdatedEventToUser(event));
        }

        logger.info("Received User Updated: {}", event);
    }

    @RabbitListener(queues = "#{userDeletedQueue.getName()}")
    @Transactional
    public void handleUserDeletedEvent(org.movierc.events.user.UserDeletedEvent event) {
        if (userRepository.existsById(event.getId())) {
            userRepository.deleteById(event.getId());
        }

        logger.info("Received User Deleted: {}", event);
    }
}
