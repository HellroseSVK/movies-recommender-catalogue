package org.movierc.movieservice.rabbit;

import lombok.AllArgsConstructor;
import org.movierc.events.EventUtil;
import org.movierc.events.movie.MovieEvents;
import org.movierc.events.movie.MovieFilmPersonRoleAddedEvent;
import org.movierc.events.movie.MovieFilmPersonRoleRemovedEvent;
import org.movierc.movieservice.configuration.RabbitConfig;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MovieFilmPersonRoleProducer {
    private final RabbitTemplate rabbitTemplate;
    private final TopicExchange topicExchange;

    public void sendMovieFilmPersonRoleAddedEvent(MovieFilmPersonRoleAddedEvent event) {
        rabbitTemplate.convertAndSend(
                topicExchange.getName(),
                EventUtil.forService(MovieEvents.MOVIE_FILM_PERSON_ROLE_ADDED, RabbitConfig.SERVICE_ID),
                event
        );
    }

    public void sendMovieFilmPersonRoleRemovedEvent(MovieFilmPersonRoleRemovedEvent event) {
        rabbitTemplate.convertAndSend(
                topicExchange.getName(),
                EventUtil.forService(MovieEvents.MOVIE_FILM_PERSON_ROLE_REMOVED, RabbitConfig.SERVICE_ID),
                event
        );
    }
}
