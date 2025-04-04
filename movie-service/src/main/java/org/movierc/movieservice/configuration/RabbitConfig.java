package org.movierc.movieservice.configuration;

import org.movierc.events.EventUtil;
import org.movierc.events.filmPerson.FilmPersonEvents;
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
    public static String SERVICE_ID = "movie-service";

    @Bean
    public TopicExchange topicExchange(String rabbitExchangeName) {
        return new TopicExchange(rabbitExchangeName);
    }

    @Bean
    public Queue filmPersonCreatedQueue() {
        return new Queue(EventUtil.forService(FilmPersonEvents.FILM_PERSON_CREATED_QUEUE, SERVICE_ID), true);
    }

    @Bean
    public Queue filmPersonUpdatedQueue() {
        return new Queue(EventUtil.forService(FilmPersonEvents.FILM_PERSON_UPDATED_QUEUE, SERVICE_ID), true);
    }

    @Bean
    public Queue filmPersonDeletedQueue() {
        return new Queue(EventUtil.forService(FilmPersonEvents.FILM_PERSON_DELETED_QUEUE, SERVICE_ID), true);
    }

    @Bean
    public Binding filmPersonCreatedBinding(Queue filmPersonCreatedQueue, TopicExchange topicExchange) {
        return BindingBuilder
                .bind(filmPersonCreatedQueue)
                .to(topicExchange)
                .with(EventUtil.all(FilmPersonEvents.FILM_PERSON_CREATED_QUEUE));
    }

    @Bean
    public Binding filmPersonUpdatedBinding(Queue filmPersonUpdatedQueue, TopicExchange topicExchange) {
        return BindingBuilder
                .bind(filmPersonUpdatedQueue)
                .to(topicExchange)
                .with(EventUtil.all(FilmPersonEvents.FILM_PERSON_UPDATED_QUEUE));
    }

    @Bean
    public Binding filmPersonDeletedBinding(Queue filmPersonDeletedQueue, TopicExchange topicExchange) {
        return BindingBuilder
                .bind(filmPersonDeletedQueue)
                .to(topicExchange)
                .with(EventUtil.all(FilmPersonEvents.FILM_PERSON_DELETED_QUEUE));
    }

    @Bean
    public Queue movieRatingCreatedQueue() {
        return new Queue(EventUtil.forService(RatingEvents.MOVIE_RATING_CREATED_QUEUE, SERVICE_ID), true);
    }

    @Bean
    public Queue movieRatingUpdatedQueue() {
        return new Queue(EventUtil.forService(RatingEvents.MOVIE_RATING_UPDATED_QUEUE, SERVICE_ID), true);
    }

    @Bean
    public Queue movieRatingDeletedQueue() {
        return new Queue(EventUtil.forService(RatingEvents.MOVIE_RATING_DELETED_QUEUE, SERVICE_ID), true);
    }

    @Bean
    public Binding movieRatingCreatedBinding(Queue movieRatingCreatedQueue, TopicExchange topicExchange) {
        return BindingBuilder
                .bind(movieRatingCreatedQueue)
                .to(topicExchange)
                .with(EventUtil.all(RatingEvents.MOVIE_RATING_CREATED_QUEUE));
    }

    @Bean
    public Binding movieRatingUpdatedBinding(Queue movieRatingUpdatedQueue, TopicExchange topicExchange) {
        return BindingBuilder
                .bind(movieRatingUpdatedQueue)
                .to(topicExchange)
                .with(EventUtil.all(RatingEvents.MOVIE_RATING_UPDATED_QUEUE));
    }

    @Bean
    public Binding movieRatingDeletedBinding(Queue movieRatingDeletedQueue, TopicExchange topicExchange) {
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
