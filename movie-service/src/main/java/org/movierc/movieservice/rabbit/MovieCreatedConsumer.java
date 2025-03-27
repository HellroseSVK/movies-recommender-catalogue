package org.movierc.movieservice.rabbit;

import lombok.AllArgsConstructor;
import org.movierc.events.filmPerson.FilmPersonCreatedEvent;
import org.movierc.events.movie.MovieCreatedEvent;
import org.movierc.movieservice.mappers.FilmPersonCreatedEventMapper;
import org.movierc.movieservice.repository.FilmPersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class MovieCreatedConsumer {
    private final Logger logger = LoggerFactory.getLogger(MovieCreatedConsumer.class);

    @RabbitListener(queues = "#{movieCreatedQueue.getName()}")
    public void handleMovieRatedEvent(@Payload MovieCreatedEvent event) {

        logger.info("Received Movie Created Created: {}", event);
    }
}
