package org.movierc.recommendationservice.rabbit;

import lombok.AllArgsConstructor;
import org.movierc.events.rating.MovieRatingCreatedEvent;
import org.movierc.events.rating.MovieRatingDeletedEvent;
import org.movierc.events.rating.MovieRatingUpdatedEvent;
import org.movierc.recommendationservice.mapper.UserMovieRatingMapper;
import org.movierc.recommendationservice.repository.UserMovieRatingRepository;
import org.slf4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UserMovieRatingEventConsumer {
    private final Logger logger = org.slf4j.LoggerFactory.getLogger(UserMovieRatingEventConsumer.class);

    private final UserMovieRatingRepository userMovieRatingRepository;

    private final UserMovieRatingMapper userMovieRatingMapper;

    @RabbitListener(queues = "#movieRatingCreatedQueue.getName()")
    public void handleMovieRatingCreatedEvent(MovieRatingCreatedEvent event) {
        if (!userMovieRatingRepository.existsByUserIdAndMovieId(event.getUserId(), event.getMovieId())) {
            userMovieRatingRepository.save(userMovieRatingMapper.userMovieRatingCreatedEventToUserMovieRating(event));
        }
        logger.info("Received Movie Rating Created: {}", event);
    }

    @RabbitListener(queues = "#movieRatingUpdatedQueue.getName()")
    public void handleMovieRatingUpdatedEvent(MovieRatingUpdatedEvent event) {
        if (!userMovieRatingRepository.existsByUserIdAndMovieId(event.getUserId(), event.getMovieId())) {
            userMovieRatingRepository.save(userMovieRatingMapper.userMovieRatingUpdatedEventToUserMovieRating(event));
        }
        logger.info("Received Movie Rating Updated: {}", event);
    }

    @RabbitListener(queues = "#movieRatingDeletedQueue.getName()")
    public void handleMovieRatingDeletedEvent(MovieRatingDeletedEvent event) {
        if (!userMovieRatingRepository.existsByUserIdAndMovieId(event.getUserId(), event.getMovieId())) {
            userMovieRatingRepository.deleteByUserIdAndMovieId(event.getUserId(), event.getMovieId());
        }
        logger.info("Received Movie Rating Deleted: {}", event);
    }


}
