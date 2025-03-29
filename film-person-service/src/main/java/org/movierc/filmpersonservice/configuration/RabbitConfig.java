package org.movierc.filmpersonservice.configuration;

import org.movierc.events.EventUtil;
import org.movierc.events.movie.MovieEvents;
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
    public static String SERVICE_ID = "film-person-service";

    @Bean
    public TopicExchange topicExchange(String rabbitExchangeName) {
        return new TopicExchange(rabbitExchangeName);
    }

    @Bean
    public Queue movieCreatedQueue() {
        return new Queue(EventUtil.forService(MovieEvents.MOVIE_CREATED_QUEUE, SERVICE_ID), true);
    }

    @Bean
    public Binding movieCreatedBinding(Queue movieCreatedQueue, TopicExchange topicExchange) {
        return BindingBuilder
                .bind(movieCreatedQueue)
                .to(topicExchange)
                .with(EventUtil.all(MovieEvents.MOVIE_CREATED_QUEUE));
    }

    @Bean
    public Queue movieUpdatedQueue() {
        return new Queue(EventUtil.forService(MovieEvents.MOVIE_UPDATED_QUEUE, SERVICE_ID), true);
    }

    @Bean
    public Binding movieUpdatedBinding(Queue movieUpdatedQueue, TopicExchange topicExchange) {
        return BindingBuilder
                .bind(movieUpdatedQueue)
                .to(topicExchange)
                .with(EventUtil.all(MovieEvents.MOVIE_UPDATED_QUEUE));
    }

    @Bean
    public Queue movieDeletedQueue() {
        return new Queue(EventUtil.forService(MovieEvents.MOVIE_DELETED_QUEUE, SERVICE_ID), true);
    }

    @Bean
    public Binding movieDeletedBinding(Queue movieDeletedQueue, TopicExchange topicExchange) {
        return BindingBuilder
                .bind(movieDeletedQueue)
                .to(topicExchange)
                .with(EventUtil.all(MovieEvents.MOVIE_DELETED_QUEUE));
    }

    @Bean
    public Queue movieFilmPersonRolesAddedQueue() {
        return new Queue(EventUtil.forService(MovieEvents.MOVIE_FILM_PERSON_ROLES_ADDED, SERVICE_ID), true);
    }

    @Bean
    public Binding movieFilmPersonRolesAddedBinding(Queue movieFilmPersonRolesAddedQueue, TopicExchange topicExchange) {
        return BindingBuilder
                .bind(movieFilmPersonRolesAddedQueue)
                .to(topicExchange)
                .with(EventUtil.all(MovieEvents.MOVIE_FILM_PERSON_ROLES_ADDED));
    }

    @Bean
    public Queue movieFilmPersonRolesRemovedQueue() {
        return new Queue(EventUtil.forService(MovieEvents.MOVIE_FILM_PERSON_ROLES_REMOVED, SERVICE_ID), true);
    }

    @Bean
    public Binding movieFilmPersonRolesRemovedBinding(Queue movieFilmPersonRolesRemovedQueue, TopicExchange topicExchange) {
        return BindingBuilder
                .bind(movieFilmPersonRolesRemovedQueue)
                .to(topicExchange)
                .with(EventUtil.all(MovieEvents.MOVIE_FILM_PERSON_ROLES_REMOVED));
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
