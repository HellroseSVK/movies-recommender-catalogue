package org.movierc.feedbackservice.artemis;

import org.movierc.events.rating.MovieRatingChangedEvent;

public interface UserMovieRatingEventArtemisProducer {
    void sendMovieRatingChangedEvent(MovieRatingChangedEvent event);
}
