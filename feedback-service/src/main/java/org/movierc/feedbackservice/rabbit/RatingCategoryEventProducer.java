package org.movierc.feedbackservice.rabbit;

import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RatingCategoryEventProducer {
    private final RabbitTemplate rabbitTemplate;

    public void sendRatingCategoryCreatedEvent(org.movierc.events.rating.RatingCategoryCreatedEvent event) {
        rabbitTemplate.convertAndSend(
                org.movierc.events.EventUtil.forService(org.movierc.events.rating.RatingEvents.RATING_CATEGORY_CREATED_QUEUE, org.movierc.feedbackservice.configuration.RabbitConfig.SERVICE_ID),
                event
        );
    }
}
