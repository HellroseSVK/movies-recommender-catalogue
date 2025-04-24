package org.movierc.feedbackservice.artemis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.apache.activemq.artemis.api.core.client.ClientMessage;
import org.apache.activemq.artemis.api.core.client.ClientSession;
import org.apache.activemq.artemis.api.core.client.ClientSessionFactory;
import org.movierc.events.rating.MovieRatingChangedEvent;
import org.movierc.events.rating.RatingEvents;
import org.springframework.boot.autoconfigure.jms.artemis.ArtemisProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
@Profile("!without-artemis")
public class RealUserMovieRatingEventArtemisProducer implements UserMovieRatingEventArtemisProducer {
    private final ArtemisProperties artemisProperties;
    // session factory is not available in tests
    private final Optional<ClientSessionFactory> sessionFactory;
    private final ObjectMapper mapper;

    public void sendMovieRatingChangedEvent(MovieRatingChangedEvent event) {
        sessionFactory.ifPresent(sf -> {
            // we need to send it as json since the other side cant use JMS
            try (ClientSession session = sf.createSession(
                    artemisProperties.getUser(),
                    artemisProperties.getPassword(),
                    false,
                    false,
                    false,
                    false,
                    0
            )) {
                ClientMessage msg = session.createMessage(true);
                msg.getBodyBuffer().writeString(mapper.writeValueAsString(event));
                session.createProducer(RatingEvents.MOVIE_RATING_CHANGED_QUEUE).send(msg);
                session.commit();
            } catch (ActiveMQException | JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
