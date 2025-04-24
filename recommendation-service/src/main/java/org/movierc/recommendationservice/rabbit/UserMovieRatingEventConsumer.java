package org.movierc.recommendationservice.rabbit;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.movierc.events.rating.MovieRatingCreatedEvent;
import org.movierc.events.rating.MovieRatingDeletedEvent;
import org.movierc.events.rating.MovieRatingUpdatedEvent;
import org.movierc.recommendationservice.entity.RatingCategory;
import org.movierc.recommendationservice.entity.RatingDetail;
import org.movierc.recommendationservice.entity.UserMovieRating;
import org.movierc.recommendationservice.exception.CategoryNotFoundException;
import org.movierc.recommendationservice.exception.MovieNotFoundException;
import org.movierc.recommendationservice.mapper.UserMovieRatingMapper;
import org.movierc.recommendationservice.repository.MovieRepository;
import org.movierc.recommendationservice.repository.RatingCategoryRepository;
import org.movierc.recommendationservice.repository.UserMovieRatingRepository;
import org.slf4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class UserMovieRatingEventConsumer {
    private final Logger logger = org.slf4j.LoggerFactory.getLogger(UserMovieRatingEventConsumer.class);

    private final UserMovieRatingRepository userMovieRatingRepository;

    private final UserMovieRatingMapper userMovieRatingMapper;
    private final RatingCategoryRepository ratingCategoryRepository;
    private final MovieRepository movieRepository;

    @RabbitListener(queues = "#{movieRatingCreatedQueue.getName()}")
    @Transactional
    public void handleMovieRatingCreatedEvent(MovieRatingCreatedEvent event) {
        if (!userMovieRatingRepository.existsByUserIdAndMovieId(event.getUserId(), event.getMovieId())) {
            var movie = movieRepository.findById(event.getMovieId()).orElseThrow(
                    () -> new MovieNotFoundException(event.getMovieId())
            );

            var userMovieRating = userMovieRatingMapper.userMovieRatingCreatedEventToUserMovieRating(event, movie);
            userMovieRating.setRatingDetails(convertRatingDetails(userMovieRating, event.getRatingDetails()));
            userMovieRatingRepository.save(userMovieRating);
        }
        logger.info("Received Movie Rating Created: {}", event);
    }

    @RabbitListener(queues = "#{movieRatingUpdatedQueue.getName()}")
    @Transactional
    public void handleMovieRatingUpdatedEvent(MovieRatingUpdatedEvent event) {
        var maybeUserMovieRating = userMovieRatingRepository.findByUserIdAndMovieId(event.getUserId(), event.getMovieId());
        if (maybeUserMovieRating.isPresent()) {
            var userMovieRating = maybeUserMovieRating.get();

            userMovieRating.setRatingDetails(convertRatingDetails(userMovieRating, event.getNewRatingDetails()));

            userMovieRatingRepository.save(userMovieRating);
        } else {
            logger.warn("User Movie Rating with User ID {} and Movie ID {} not found for update", event.getUserId(), event.getMovieId());
        }

        logger.info("Received Movie Rating Updated: {}", event);
    }

    @RabbitListener(queues = "#{movieRatingDeletedQueue.getName()}")
    @Transactional
    public void handleMovieRatingDeletedEvent(MovieRatingDeletedEvent event) {
        if (!userMovieRatingRepository.existsByUserIdAndMovieId(event.getUserId(), event.getMovieId())) {
            userMovieRatingRepository.deleteByUserIdAndMovieId(event.getUserId(), event.getMovieId());
        }
        logger.info("Received Movie Rating Deleted: {}", event);
    }

    private List<RatingDetail> convertRatingDetails(UserMovieRating userMovieRating, List<org.movierc.events.rating.RatingDetail> originalRatingDetails) {
        var ratingCategoryIds = originalRatingDetails.stream().map(org.movierc.events.rating.RatingDetail::getRatingCategoryId).collect(Collectors.toSet());

        var ratingCategoryById = ratingCategoryRepository
                .findByIdIn(ratingCategoryIds)
                .stream()
                .collect(Collectors.toMap(RatingCategory::getId, v -> v));

        return originalRatingDetails.stream()
                .map(ratingDetail ->
                        RatingDetail
                                .builder()
                                .rating(ratingDetail.getRating())
                                .ratingCategory(
                                        Optional.ofNullable(ratingCategoryById.get(ratingDetail.getRatingCategoryId()))
                                                .orElseThrow(() -> new CategoryNotFoundException(ratingDetail.getRatingCategoryId()))
                                )
                                .userMovieRating(userMovieRating)
                                .build()
                )
                .toList();
    }
}
