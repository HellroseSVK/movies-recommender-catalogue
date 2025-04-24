package org.movierc.feedbackservice.rabbit;

import lombok.AllArgsConstructor;
import org.movierc.events.movie.MovieCreatedEvent;
import org.movierc.events.movie.MovieDeletedEvent;
import org.movierc.events.movie.MovieUpdatedEvent;
import org.movierc.feedbackservice.mapper.MovieEventMapper;
import org.movierc.feedbackservice.repository.MovieRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@AllArgsConstructor
public class MovieEventConsumer {
    private final Logger logger = LoggerFactory.getLogger(MovieEventConsumer.class);

    private final MovieRepository movieRepository;

    private final MovieEventMapper movieEventMapper;

    @RabbitListener(queues = "#{movieCreatedQueue.getName()}")
    @Transactional
    public void handleMovieCreatedEvent(@Payload MovieCreatedEvent event) {
        if (!movieRepository.existsById(event.getId())) {
            movieRepository.save(movieEventMapper.movieCreatedEventToMovie(event));
        }

        logger.info("Received Movie Created: {}", event);
    }

    @RabbitListener(queues = "#{movieUpdatedQueue.getName()}")
    @Transactional
    public void handleMovieUpdatedEvent(@Payload MovieUpdatedEvent event) {
        if (movieRepository.existsById(event.getId())) {
            movieRepository.save(movieEventMapper.movieUpdatedEventToMovie(event));
        }

        logger.info("Received Movie Updated: {}", event);
    }

    @RabbitListener(queues = "#{movieDeletedQueue.getName()}")
    @Transactional
    public void handleMovieDeletedEvent(@Payload MovieDeletedEvent event) {
        if (movieRepository.existsById(event.getId())) {
            movieRepository.deleteById(event.getId());
        }

        logger.info("Received Movie Deleted: {}", event);
    }
}
