package org.movierc.movieservice.rabbit;

import lombok.AllArgsConstructor;
import org.movierc.events.filmPerson.FilmPersonCreatedEvent;
import org.movierc.movieservice.mapper.FilmPersonCreatedEventMapper;
import org.movierc.movieservice.repository.FilmPersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class FilmPersonEventConsumer {
    private final Logger logger = LoggerFactory.getLogger(FilmPersonEventConsumer.class);

    private final FilmPersonCreatedEventMapper filmPersonCreatedEventMapper;
    private final FilmPersonRepository filmPersonRepository;

    @RabbitListener(queues = "#{filmPersonCreatedQueue.getName()}")
    public void handleMovieRatedEvent(@Payload FilmPersonCreatedEvent event) {
        if (filmPersonRepository.findById(event.getId()).isEmpty()) {
            filmPersonRepository.save(filmPersonCreatedEventMapper.filmPersonCreatedEventToFilmPerson(event));
        }

        logger.info("Received Film Person Created: {}", event);
    }
}
