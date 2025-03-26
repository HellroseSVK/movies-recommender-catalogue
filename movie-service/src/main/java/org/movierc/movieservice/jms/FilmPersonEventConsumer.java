package org.movierc.movieservice.jms;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.movierc.events.filmPerson.FilmPersonCreatedEvent;
import org.movierc.events.filmPerson.FilmPersonEvents;
import org.movierc.movieservice.entity.FilmPerson;
import org.movierc.movieservice.repository.FilmPersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class FilmPersonEventConsumer {
    private final Logger logger = LoggerFactory.getLogger(FilmPersonEventConsumer.class);

    private final ModelMapper modelMapper;
    private final FilmPersonRepository filmPersonRepository;

    @JmsListener(destination = FilmPersonEvents.FILM_PERSON_CREATED_QUEUE)
    public void handleMovieRatedEvent(@Payload FilmPersonCreatedEvent event) {
        if (filmPersonRepository.findById(event.getId()).isEmpty()) {
            filmPersonRepository.save(modelMapper.map(event, FilmPerson.class));
        }

        logger.info("Received Film Person Created: {}", event);
    }
}
