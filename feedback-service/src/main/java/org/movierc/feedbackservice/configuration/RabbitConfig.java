package org.movierc.feedbackservice.configuration;

import org.movierc.events.EventUtil;
import org.movierc.events.movie.MovieEvents;
import org.movierc.events.user.UserEvents;
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
    public Queue userCreatedQueue() {
        return new Queue(EventUtil.forService(UserEvents.USER_CREATED_QUEUE, SERVICE_ID), true);
    }

    @Bean
    public Queue userUpdatedQueue() {
        return new Queue(EventUtil.forService(UserEvents.USER_UPDATED_QUEUE, SERVICE_ID), true);
    }

    @Bean
    public Queue userDeletedQueue() {
        return new Queue(EventUtil.forService(UserEvents.USER_DELETED_QUEUE, SERVICE_ID), true);
    }

    @Bean
    public Binding userCreatedBinding(Queue userCreatedQueue, TopicExchange topicExchange) {
        return BindingBuilder
                .bind(userCreatedQueue)
                .to(topicExchange)
                .with(EventUtil.all(UserEvents.USER_CREATED_QUEUE));
    }

    @Bean
    public Binding userUpdatedBinding(Queue userUpdatedQueue, TopicExchange topicExchange) {
        return BindingBuilder
                .bind(userUpdatedQueue)
                .to(topicExchange)
                .with(EventUtil.all(UserEvents.USER_UPDATED_QUEUE));
    }

    @Bean
    public Binding userDeletedBinding(Queue userDeletedQueue, TopicExchange topicExchange) {
        return BindingBuilder
                .bind(userDeletedQueue)
                .to(topicExchange)
                .with(EventUtil.all(UserEvents.USER_DELETED_QUEUE));
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


    @Bean
    public MessageConverter jsonMessageConverter() {
        DefaultClassMapper defaultClassMapper = new DefaultClassMapper();
        defaultClassMapper.setTrustedPackages("*");
        Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
        jackson2JsonMessageConverter.setClassMapper(defaultClassMapper);
        return jackson2JsonMessageConverter;
    }
}
