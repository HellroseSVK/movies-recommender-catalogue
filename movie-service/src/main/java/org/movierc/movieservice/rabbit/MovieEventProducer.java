package org.movierc.movieservice.rabbit;

import lombok.AllArgsConstructor;
import org.movierc.events.EventUtil;
import org.movierc.events.movie.MovieCreatedEvent;
import org.movierc.events.movie.MovieDeletedEvent;
import org.movierc.events.movie.MovieEvents;
import org.movierc.events.movie.MovieUpdatedEvent;
import org.movierc.movieservice.configuration.RabbitConfig;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@AllArgsConstructor
public class MovieEventProducer {
    private final RabbitTemplate rabbitTemplate;
    private final TopicExchange topicExchange;

    public void sendMovieCreatedEvent(MovieCreatedEvent event) {
        rabbitTemplate.convertAndSend(
                topicExchange.getName(),
                EventUtil.forService(MovieEvents.MOVIE_CREATED_QUEUE, RabbitConfig.SERVICE_ID),
                event
        );
    }

    public void sendMovieUpdatedEvent(MovieUpdatedEvent event) {
        rabbitTemplate.convertAndSend(
                topicExchange.getName(),
                EventUtil.forService(MovieEvents.MOVIE_UPDATED_QUEUE, RabbitConfig.SERVICE_ID),
                event
        );
    }

    public void sendMovieDeletedEvent(MovieDeletedEvent event) {
        rabbitTemplate.convertAndSend(
                topicExchange.getName(),
                EventUtil.forService(MovieEvents.MOVIE_DELETED_QUEUE, RabbitConfig.SERVICE_ID),
                event
        );
    }
}
