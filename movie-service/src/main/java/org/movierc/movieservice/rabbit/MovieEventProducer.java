package org.movierc.movieservice.rabbit;

import lombok.AllArgsConstructor;
import org.movierc.events.EventUtil;
import org.movierc.events.movie.MovieCreatedEvent;
import org.movierc.events.movie.MovieEvents;
import org.movierc.movieservice.configuration.RabbitConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MovieEventProducer {
    private final RabbitTemplate rabbitTemplate;

    public void sendMovieCreatedEvent(MovieCreatedEvent event) {
        rabbitTemplate.convertAndSend(
                EventUtil.forService(MovieEvents.MOVIE_CREATED_QUEUE, RabbitConfig.SERVICE_ID),
                event
        );
    }
}
