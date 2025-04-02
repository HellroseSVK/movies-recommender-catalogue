package org.movierc.recommendationservice.rabbit;

import lombok.AllArgsConstructor;
import org.movierc.events.rating.RatingCategoryCreatedEvent;
import org.movierc.recommendationservice.mapper.RatingCategoryMapper;
import org.movierc.recommendationservice.repository.RatingCategoryRepository;
import org.slf4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RatingCategoryEventConsumer {
    private final Logger logger = org.slf4j.LoggerFactory.getLogger(RatingCategoryEventConsumer.class);

    private final RatingCategoryRepository ratingCategoryRepository;

    private final RatingCategoryMapper ratingCategoryMapper;

    @RabbitListener(queues = "#{ratingCategoryCreatedQueue.getName()}")
    public void handleRatingCategoryCreatedEvent(RatingCategoryCreatedEvent event) {
        if (!ratingCategoryRepository.existsById(event.getId())) {
            ratingCategoryRepository.save(ratingCategoryMapper.ratingCategoryCreatedEventToRatingCategory(event));
        }

        logger.info("Received Rating Category Created: {}", event);
    }
}
