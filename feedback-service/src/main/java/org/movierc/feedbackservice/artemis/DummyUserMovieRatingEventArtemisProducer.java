package org.movierc.feedbackservice.artemis;

import org.movierc.events.rating.MovieRatingChangedEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("without-artemis")
public class DummyUserMovieRatingEventArtemisProducer implements UserMovieRatingEventArtemisProducer {
    @Override
    public void sendMovieRatingChangedEvent(MovieRatingChangedEvent event) {
        // do nothing
    }
}
