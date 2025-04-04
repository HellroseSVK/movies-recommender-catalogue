package org.movierc.filmpersonservice.rabbit;

import lombok.AllArgsConstructor;
import org.movierc.events.EventUtil;
import org.movierc.events.filmPerson.FilmPersonCreatedEvent;
import org.movierc.events.filmPerson.FilmPersonDeletedEvent;
import org.movierc.events.filmPerson.FilmPersonEvents;
import org.movierc.events.filmPerson.FilmPersonUpdatedEvent;
import org.movierc.filmpersonservice.configuration.RabbitConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class FilmPersonEventProducer {
    private final RabbitTemplate rabbitTemplate;
    private final TopicExchange topicExchange;

    public void sendFilmPersonCreatedEvent(FilmPersonCreatedEvent event) {
        rabbitTemplate.convertAndSend(
                topicExchange.getName(),
                EventUtil.forService(FilmPersonEvents.FILM_PERSON_CREATED_QUEUE, RabbitConfig.SERVICE_ID),
                event
        );
    }

    public void sendFilmPersonUpdatedEvent(FilmPersonUpdatedEvent event) {
        rabbitTemplate.convertAndSend(
                topicExchange.getName(),
                EventUtil.forService(FilmPersonEvents.FILM_PERSON_UPDATED_QUEUE, RabbitConfig.SERVICE_ID),
                event
        );
    }

    public void sendFilmPersonDeletedEvent(FilmPersonDeletedEvent event) {
        rabbitTemplate.convertAndSend(
                topicExchange.getName(),
                EventUtil.forService(FilmPersonEvents.FILM_PERSON_DELETED_QUEUE, RabbitConfig.SERVICE_ID),
                event
        );
    }
}
