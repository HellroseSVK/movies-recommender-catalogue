package org.movierc.movieservice.rabbit;

import lombok.AllArgsConstructor;
import org.movierc.events.EventUtil;
import org.movierc.events.genre.GenreCreatedEvent;
import org.movierc.events.genre.GenreDeletedEvent;
import org.movierc.events.genre.GenreEvents;
import org.movierc.events.movie.MovieCreatedEvent;
import org.movierc.events.movie.MovieDeletedEvent;
import org.movierc.events.movie.MovieEvents;
import org.movierc.events.movie.MovieUpdatedEvent;
import org.movierc.movieservice.configuration.RabbitConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class GenreEventProducer {
    private final RabbitTemplate rabbitTemplate;

    public void sendGenreCreatedEvent(GenreCreatedEvent event) {
        rabbitTemplate.convertAndSend(
                EventUtil.forService(GenreEvents.GENRE_CREATED_QUEUE, RabbitConfig.SERVICE_ID),
                event
        );
    }

    public void sendGenreDeletedEvent(GenreDeletedEvent event) {
        rabbitTemplate.convertAndSend(
                EventUtil.forService(GenreEvents.GENRE_DELETED_QUEUE, RabbitConfig.SERVICE_ID),
                event
        );
    }
}
