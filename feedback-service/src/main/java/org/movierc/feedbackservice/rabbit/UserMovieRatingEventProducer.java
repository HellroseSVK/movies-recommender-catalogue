package org.movierc.feedbackservice.rabbit;

import lombok.AllArgsConstructor;
import org.movierc.events.EventUtil;
import org.movierc.events.rating.MovieRatingCreatedEvent;
import org.movierc.events.rating.MovieRatingDeletedEvent;
import org.movierc.events.rating.MovieRatingUpdatedEvent;
import org.movierc.events.rating.RatingEvents;
import org.movierc.feedbackservice.configuration.RabbitConfig;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserMovieRatingEventProducer {
    private final RabbitTemplate rabbitTemplate;
    private final TopicExchange topicExchange;

    public void sendMovieRatingCreatedEvent(MovieRatingCreatedEvent event) {
        rabbitTemplate.convertAndSend(
                topicExchange.getName(),
                EventUtil.forService(RatingEvents.MOVIE_RATING_CREATED_QUEUE, RabbitConfig.SERVICE_ID),
                event
        );
    }

    public void sendMovieRatingUpdatedEvent(MovieRatingUpdatedEvent event) {
        rabbitTemplate.convertAndSend(
                topicExchange.getName(),
                EventUtil.forService(RatingEvents.MOVIE_RATING_UPDATED_QUEUE, RabbitConfig.SERVICE_ID),
                event
        );
    }

    public void sendMovieRatingDeletedEvent(MovieRatingDeletedEvent event) {
        rabbitTemplate.convertAndSend(
                topicExchange.getName(),
                EventUtil.forService(RatingEvents.MOVIE_RATING_DELETED_QUEUE, RabbitConfig.SERVICE_ID),
                event
        );
    }
}
