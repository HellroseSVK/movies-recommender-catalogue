package org.movierc.movieservice.rabbit;

import lombok.AllArgsConstructor;
import org.movierc.events.rating.MovieRatingCreatedEvent;
import org.movierc.events.rating.MovieRatingUpdatedEvent;
import org.movierc.movieservice.repository.MovieRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class RatingEventConsumer {
    private final Logger logger = LoggerFactory.getLogger(RatingEventConsumer.class);

    private final MovieRepository movieRepository;

    @RabbitListener(queues = "#{movieRatingCreatedQueue.getName()}")
    public void handleMovieRatedEvent(@Payload MovieRatingCreatedEvent event) {
        var movie = movieRepository.findById(event.getMovieId());

        movie.ifPresent(m -> {
            m.setTotalRating(m.getTotalRating() + event.getRating());
            m.setNumberOfRatings(m.getNumberOfRatings() + 1);
            movieRepository.save(m);
        });

        logger.info("Received Movie Rated Event: {}", event);
    }

    @RabbitListener(queues = "#{movieRatingUpdatedQueue.getName()}")
    public void handleMovieRatingUpdatedEvent(@Payload MovieRatingUpdatedEvent event) {
        var movie = movieRepository.findById(event.getMovieId());

        movie.ifPresent(m -> {
            m.setTotalRating(m.getTotalRating() + event.getNewRating() - event.getOldRating());
            movieRepository.save(m);
        });

        logger.info("Received Movie Rating Updated Event: {}", event);
    }

    @RabbitListener(queues = "#{movieRatingDeletedQueue.getName()}")
    public void handleMovieRatingDeletedEvent(@Payload MovieRatingCreatedEvent event) {
        var movie = movieRepository.findById(event.getMovieId());

        movie.ifPresent(m -> {
            m.setTotalRating(m.getTotalRating() - event.getRating());
            m.setNumberOfRatings(m.getNumberOfRatings() - 1);
            movieRepository.save(m);
        });

        logger.info("Received Movie Rating Deleted Event: {}", event);
    }
}
