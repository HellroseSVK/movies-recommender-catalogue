package org.movierc.recommendationservice.artemis;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.apache.activemq.artemis.api.core.QueueConfiguration;
import org.apache.activemq.artemis.api.core.RoutingType;
import org.apache.activemq.artemis.api.core.SimpleString;
import org.apache.activemq.artemis.api.core.client.ClientConsumer;
import org.apache.activemq.artemis.api.core.client.ClientMessage;
import org.apache.activemq.artemis.api.core.client.ClientSession;
import org.apache.activemq.artemis.api.core.client.ClientSessionFactory;
import org.movierc.events.rating.MovieRatingChangedEvent;
import org.movierc.events.rating.RatingEvents;
import org.movierc.recommendationservice.entity.RatingCategory;
import org.movierc.recommendationservice.entity.RatingDetail;
import org.movierc.recommendationservice.entity.UserMovieRating;
import org.movierc.recommendationservice.exception.CategoryNotFoundException;
import org.movierc.recommendationservice.exception.MovieNotFoundException;
import org.movierc.recommendationservice.repository.MovieRepository;
import org.movierc.recommendationservice.repository.RatingCategoryRepository;
import org.movierc.recommendationservice.repository.UserMovieRatingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jms.artemis.ArtemisProperties;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Component
public class BatchedRatingConsumer implements InitializingBean {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final int batchSize;
    private final int timeoutMs;
    private final ArtemisProperties artemisProperties;
    private final ClientSessionFactory sf;
    private final ObjectMapper mapper;
    private final MovieRepository movieRepository;
    private final RatingCategoryRepository ratingCategoryRepository;
    private final UserMovieRatingRepository userMovieRatingRepository;
    private final TransactionTemplate transactionTemplate;

    private final Logger logger = LoggerFactory.getLogger(BatchedRatingConsumer.class);

    private volatile boolean running = true;

    public BatchedRatingConsumer(
            @Value("${batch.size}")
            int batchSize,
            @Value("${batch.timeout-ms}")
            int timeoutMs,
            ArtemisProperties artemisProperties,
            ClientSessionFactory sf,
            ObjectMapper mapper,
            MovieRepository movieRepository,
            RatingCategoryRepository ratingCategoryRepository,
            UserMovieRatingRepository userMovieRatingRepository,
            PlatformTransactionManager transactionManager
    ) {
        this.batchSize = batchSize;
        this.timeoutMs = timeoutMs;
        this.artemisProperties = artemisProperties;
        this.sf = sf;
        this.mapper = mapper;
        this.movieRepository = movieRepository;
        this.ratingCategoryRepository = ratingCategoryRepository;
        this.userMovieRatingRepository = userMovieRatingRepository;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @PostConstruct
    public void start() {
        executor.submit(this::loop);
    }

    @PreDestroy
    public void stop() {
        running = false;
        executor.shutdownNow();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
    }

    private void loop() {
        try (ClientSession session = sf.createSession(
                artemisProperties.getUser(),
                artemisProperties.getPassword(),
                false,
                false,
                false,
                false,
                0
        )) {
            ensureQueue(session);
            ClientConsumer consumer = session.createConsumer(RatingEvents.MOVIE_RATING_CHANGED_QUEUE);
            session.start();

            var buffer = new ArrayList<ClientMessage>(batchSize);
            while (running) {
                ClientMessage m = consumer.receive(timeoutMs);
                if (m != null) buffer.add(m);

                if (!buffer.isEmpty() &&
                        (buffer.size() >= batchSize || m == null)) {

                    if (buffer.size() < batchSize) {
                        logger.info("Received {} messages, timeout elapsed before whole batch was received", buffer.size());
                    } else {
                        logger.info("Received {} messages, processing batch", buffer.size());
                    }

                    transactionTemplate.executeWithoutResult(
                            status -> {
                                try {
                                    processBatch(buffer, session);
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            }
                    );
                    buffer.clear();
                }
            }
        } catch (Exception e) {
            logger.error("Error in rating consumer loop", e);
        }
    }

    private void ensureQueue(ClientSession session) throws ActiveMQException {
        if (session.queueQuery(SimpleString.of(RatingEvents.MOVIE_RATING_CHANGED_QUEUE)).isExists()) {
            return;
        }

        logger.info("Queue {} does not exist, creating it", RatingEvents.MOVIE_RATING_CHANGED_QUEUE);
        session.createQueue(
                QueueConfiguration.of(RatingEvents.MOVIE_RATING_CHANGED_QUEUE)
                        .setDurable(true)
                        .setRoutingType(RoutingType.ANYCAST)
        );
        logger.info("Created queue {}", RatingEvents.MOVIE_RATING_CHANGED_QUEUE);
    }

    void processBatch(List<ClientMessage> messages, ClientSession session) throws Exception {
        try {
            logger.info("Processing batch of {} messages", messages.size());
            for (ClientMessage m : messages) {
                var event = mapper.readValue(m.getBodyBuffer().readString(), MovieRatingChangedEvent.class);

                logger.info("Processing event: {}", event);

                processEvent(event);

                m.acknowledge();
            }
            logger.info("Processed {} messages", messages.size());
            session.commit();
        } catch (Exception e) {
            logger.error("Error processing batch", e);
            session.rollback();
            throw e;
        }
    }

    private void processEvent(MovieRatingChangedEvent event) {
        var existingRating = userMovieRatingRepository.findByUserIdAndMovieId(event.getUserId(), event.getMovieId());

        if (existingRating.isPresent()) {
            var userMovieRating = existingRating.get();

            if (event.getNewRatingDetails().isEmpty()) {
                userMovieRatingRepository.delete(userMovieRating);
            } else {
                var ratingDetails = createRatingDetails(event);
                userMovieRating.setRatingDetails(ratingDetails);
                ratingDetails.forEach(rd -> {
                    rd.setUserMovieRating(userMovieRating);
                });

                userMovieRatingRepository.save(userMovieRating);
            }
        } else {
            var movie = movieRepository.findById(event.getMovieId()).orElseThrow(
                    () -> new MovieNotFoundException(event.getMovieId())
            );

            var ratingDetails = createRatingDetails(event);

            var newUserMovieRating = UserMovieRating.builder()
                    .userId(event.getUserId())
                    .movie(movie)
                    .ratingDetails(ratingDetails)
                    .build();
            ratingDetails.forEach(rd -> rd.setUserMovieRating(newUserMovieRating));

            userMovieRatingRepository.save(newUserMovieRating);
        }
    }

    private List<RatingDetail> createRatingDetails(MovieRatingChangedEvent event) {
        var ratingCategoryIds = event
                .getNewRatingDetails()
                .stream()
                .map(org.movierc.events.rating.RatingDetail::getRatingCategoryId)
                .collect(Collectors.toSet());

        var ratingCategoryById = ratingCategoryRepository
                .findByIdIn(ratingCategoryIds)
                .stream()
                .collect(Collectors.toMap(RatingCategory::getId, v -> v));

        return event.getNewRatingDetails()
                .stream()
                .map(
                        rd -> RatingDetail.builder()
                                .ratingCategory(
                                        Optional.ofNullable(ratingCategoryById.get(rd.getRatingCategoryId()))
                                                .orElseThrow(() -> new CategoryNotFoundException(rd.getRatingCategoryId()))
                                )
                                .rating(rd.getRating())
                                .build()
                )
                .toList();
    }
}

