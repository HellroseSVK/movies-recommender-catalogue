package org.movierc.recommendationservice.configuration;

import org.movierc.events.EventUtil;
import org.movierc.events.movie.MovieEvents;
import org.movierc.events.rating.RatingEvents;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    public static String SERVICE_ID = "feedback-service";

    @Bean
    public TopicExchange topicExchange(String rabbitExchangeName) {
        return new TopicExchange(rabbitExchangeName);
    }


    @Bean
    public Queue movieCreatedQueue() {
        return new Queue(EventUtil.forService(MovieEvents.MOVIE_CREATED_QUEUE, SERVICE_ID), true);
    }

    @Bean
    public Queue movieUpdatedQueue() {
        return new Queue(EventUtil.forService(MovieEvents.MOVIE_UPDATED_QUEUE, SERVICE_ID), true);
    }

    @Bean
    public Queue movieDeletedQueue() {
        return new Queue(EventUtil.forService(MovieEvents.MOVIE_DELETED_QUEUE, SERVICE_ID), true);
    }

    @Bean
    public Binding movieCreatedBinding(Queue movieCreatedQueue, TopicExchange topicExchange) {
        return BindingBuilder
                .bind(movieCreatedQueue)
                .to(topicExchange)
                .with(EventUtil.all(MovieEvents.MOVIE_CREATED_QUEUE));
    }

    @Bean
    public Binding movieUpdatedBinding(Queue movieUpdatedQueue, TopicExchange topicExchange) {
        return BindingBuilder
                .bind(movieUpdatedQueue)
                .to(topicExchange)
                .with(EventUtil.all(MovieEvents.MOVIE_UPDATED_QUEUE));
    }

    @Bean
    public Binding movieDeletedBinding(Queue movieDeletedQueue, TopicExchange topicExchange) {
        return BindingBuilder
                .bind(movieDeletedQueue)
                .to(topicExchange)
                .with(EventUtil.all(MovieEvents.MOVIE_DELETED_QUEUE));
    }

    @Bean Queue ratingCategoryCreatedQueue() {
        return new Queue(EventUtil.forService(RatingEvents.RATING_CATEGORY_CREATED_QUEUE, SERVICE_ID), true);
    }

    @Bean Binding ratingCategoryCreatedBinding(Queue ratingCategoryCreatedQueue, TopicExchange topicExchange) {
        return BindingBuilder
                .bind(ratingCategoryCreatedQueue)
                .to(topicExchange)
                .with(EventUtil.all(RatingEvents.RATING_CATEGORY_CREATED_QUEUE));
    }

    @Bean Queue movieRatingCreatedQueue() {
        return new Queue(EventUtil.forService(RatingEvents.MOVIE_RATING_CREATED_QUEUE, SERVICE_ID), true);
    }

    @Bean Queue movieRatingUpdatedQueue() {
        return new Queue(EventUtil.forService(RatingEvents.MOVIE_RATING_UPDATED_QUEUE, SERVICE_ID), true);
    }

    @Bean Queue movieRatingDeletedQueue() {
        return new Queue(EventUtil.forService(RatingEvents.MOVIE_RATING_DELETED_QUEUE, SERVICE_ID), true);
    }

    @Bean Binding movieRatingCreatedBinding(Queue movieRatingCreatedQueue, TopicExchange topicExchange) {
        return BindingBuilder
                .bind(movieRatingCreatedQueue)
                .to(topicExchange)
                .with(EventUtil.all(RatingEvents.MOVIE_RATING_CREATED_QUEUE));
    }

    @Bean Binding movieRatingUpdatedBinding(Queue movieRatingUpdatedQueue, TopicExchange topicExchange) {
        return BindingBuilder
                .bind(movieRatingUpdatedQueue)
                .to(topicExchange)
                .with(EventUtil.all(RatingEvents.MOVIE_RATING_UPDATED_QUEUE));
    }

    @Bean Binding movieRatingDeletedBinding(Queue movieRatingDeletedQueue, TopicExchange topicExchange) {
        return BindingBuilder
                .bind(movieRatingDeletedQueue)
                .to(topicExchange)
                .with(EventUtil.all(RatingEvents.MOVIE_RATING_DELETED_QUEUE));
    }


    @Bean
    public MessageConverter jsonMessageConverter() {
        DefaultClassMapper defaultClassMapper = new DefaultClassMapper();
        defaultClassMapper.setTrustedPackages("*");
        Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
        jackson2JsonMessageConverter.setClassMapper(defaultClassMapper);
        return jackson2JsonMessageConverter;
    }
}
