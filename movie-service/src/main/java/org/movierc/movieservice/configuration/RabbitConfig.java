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
    public Queue movieRatedQueue() {
        return new Queue(EventUtil.forService(RatingEvents.MOVIE_RATED_QUEUE, SERVICE_ID), true);
    }

    @Bean
    public Binding filmPersonCreatedBinding(Queue filmPersonCreatedQueue, TopicExchange topicExchange) {
        return BindingBuilder
                .bind(filmPersonCreatedQueue)
                .to(topicExchange)
                .with(EventUtil.all(FilmPersonEvents.FILM_PERSON_CREATED_QUEUE));
    }

    @Bean
    public Binding movieRatedBinding(Queue movieRatedQueue, TopicExchange topicExchange) {
        return BindingBuilder
                .bind(movieRatedQueue)
                .to(topicExchange)
                .with(EventUtil.all(RatingEvents.MOVIE_RATED_QUEUE));
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
