package org.movierc.movieservice.rabbit;

import lombok.AllArgsConstructor;
import org.movierc.events.filmPerson.FilmPersonCreatedEvent;
import org.movierc.events.filmPerson.FilmPersonDeletedEvent;
import org.movierc.events.filmPerson.FilmPersonUpdatedEvent;
import org.movierc.movieservice.mapper.FilmPersonCreatedEventMapper;
import org.movierc.movieservice.repository.FilmPersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Transactional
@Component
@AllArgsConstructor
public class FilmPersonEventConsumer {
    private final Logger logger = LoggerFactory.getLogger(FilmPersonEventConsumer.class);

    private final FilmPersonCreatedEventMapper filmPersonCreatedEventMapper;
    private final FilmPersonRepository filmPersonRepository;

    @RabbitListener(queues = "#{filmPersonCreatedQueue.getName()}")
    public void handleFilmPersonCreatedEvent(@Payload FilmPersonCreatedEvent event) {
        if (filmPersonRepository.findById(event.getId()).isEmpty()) {
            filmPersonRepository.save(filmPersonCreatedEventMapper.filmPersonCreatedEventToFilmPerson(event));
        }

        logger.info("Received Film Person Created: {}", event);
    }

    @RabbitListener(queues = "#{filmPersonUpdatedQueue.getName()}")
    public void handleFilmPersonUpdatedEvent(@Payload FilmPersonUpdatedEvent event) {
        var maybeFilmPerson = filmPersonRepository.findById(event.getId());

        if (maybeFilmPerson.isEmpty()) {
            logger.warn("Film Person with ID {} not found for update", event.getId());
            return;
        }

        maybeFilmPerson.ifPresent(filmPerson -> {
            filmPerson.setName(event.getName());
            filmPerson.setImage(event.getImage());
            filmPerson.setDateOfBirth(event.getDateOfBirth());
            filmPerson.setBiography(event.getBiography());

            filmPersonRepository.save(filmPerson);
        });

        logger.info("Received Film Person Updated: {}", event);
    }

    @RabbitListener(queues = "#{filmPersonDeletedQueue.getName()}")
    public void handleFilmPersonDeletedEvent(@Payload FilmPersonDeletedEvent event) {
        filmPersonRepository.deleteById(event.getId());

        logger.info("Received Film Person Deleted: {}", event);
    }
}
