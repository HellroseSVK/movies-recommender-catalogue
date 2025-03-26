package org.movierc.movieservice.jms;

import lombok.AllArgsConstructor;
import org.movierc.events.movie.MovieCreatedEvent;
import org.movierc.events.movie.MovieEvents;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MovieEventProducer {
    private final JmsTemplate jmsTemplate;

    public void sendMovieCreatedEvent(MovieCreatedEvent event) {
        jmsTemplate.convertAndSend(MovieEvents.MOVIE_CREATED_QUEUE, event);
    }
}
