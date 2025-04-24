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
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Transactional
@Component
@AllArgsConstructor
public class RatingEventConsumer {
    private final Logger logger = LoggerFactory.getLogger(RatingEventConsumer.class);

    private final MovieRepository movieRepository;

    @RabbitListener(queues = "#{movieRatingCreatedQueue.getName()}")
    public void handleMovieRatedEvent(@Payload MovieRatingCreatedEvent event) {
        var movie = movieRepository.findById(event.getMovieId());

        movie.ifPresent(m -> {
            Map<UUID, Integer> totalRatings = m.getTotalRatings();
            Map<UUID, Integer> numbersOfRatings = m.getNumbersOfRatings();

            event.getRatingDetails()
                    .forEach(ratingDetail -> {
                        if (ratingDetail.getRatingCategoryId() != null) {
                            UUID categoryId = ratingDetail.getRatingCategoryId();
                            totalRatings.put(categoryId, totalRatings.getOrDefault(categoryId, 0) + ratingDetail.getRating());
                            numbersOfRatings.put(categoryId, numbersOfRatings.getOrDefault(categoryId, 0) + 1);
                        }
                    });
            movieRepository.save(m);
        });

        logger.info("Received Movie Rated Event: {}", event);
    }

    @RabbitListener(queues = "#{movieRatingUpdatedQueue.getName()}")
    public void handleMovieRatingUpdatedEvent(@Payload MovieRatingUpdatedEvent event) {
        var movie = movieRepository.findById(event.getMovieId());

        movie.ifPresent(m -> {
            Map<UUID, Integer> totalRatings = m.getTotalRatings();
            Map<UUID, Integer> numbersOfRatings = m.getNumbersOfRatings();

            event.getOldRatingDetails()
                    .forEach(ratingDetail -> {
                        if (ratingDetail.getRatingCategoryId() != null) {
                            UUID categoryId = ratingDetail.getRatingCategoryId();
                            totalRatings.put(categoryId, totalRatings.getOrDefault(categoryId, ratingDetail.getRating()) - ratingDetail.getRating());
                            numbersOfRatings.put(categoryId, numbersOfRatings.getOrDefault(categoryId, 1) - 1);
                        }
                    });
            event.getNewRatingDetails()
                    .forEach(ratingDetail -> {
                        if (ratingDetail.getRatingCategoryId() != null) {
                            UUID categoryId = ratingDetail.getRatingCategoryId();
                            totalRatings.put(categoryId, totalRatings.getOrDefault(categoryId, 0) + ratingDetail.getRating());
                            numbersOfRatings.put(categoryId, numbersOfRatings.getOrDefault(categoryId, 0) + 1);
                        }
                    });

            movieRepository.save(m);
        });

        logger.info("Received Movie Rating Updated Event: {}", event);
    }

    @RabbitListener(queues = "#{movieRatingDeletedQueue.getName()}")
    public void handleMovieRatingDeletedEvent(@Payload MovieRatingCreatedEvent event) {
        var movie = movieRepository.findById(event.getMovieId());

        movie.ifPresent(m -> {
            Map<UUID, Integer> totalRatings = m.getTotalRatings();
            Map<UUID, Integer> numbersOfRatings = m.getNumbersOfRatings();

            event.getRatingDetails()
                    .forEach(ratingDetail -> {
                        if (ratingDetail.getRatingCategoryId() != null) {
                            UUID categoryId = ratingDetail.getRatingCategoryId();
                            totalRatings.put(categoryId, totalRatings.getOrDefault(categoryId, ratingDetail.getRating()) - ratingDetail.getRating());
                            numbersOfRatings.put(categoryId, numbersOfRatings.getOrDefault(categoryId, 1) - 1);
                        }
                    });
            movieRepository.save(m);
        });

        logger.info("Received Movie Rating Deleted Event: {}", event);
    }
}
