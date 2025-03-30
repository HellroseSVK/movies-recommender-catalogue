package org.movierc.filmpersonservice.rabbit;

import lombok.AllArgsConstructor;
import org.movierc.events.movie.MovieCreatedEvent;
import org.movierc.events.movie.MovieFilmPersonRolesAddedEvent;
import org.movierc.events.movie.MovieFilmPersonRolesRemovedEvent;
import org.movierc.events.movie.MovieUpdatedEvent;
import org.movierc.filmpersonservice.mapper.FilmPersonMovieAssignmentMapper;
import org.movierc.filmpersonservice.mapper.MovieEventMapper;
import org.movierc.filmpersonservice.repository.FilmPersonRepository;
import org.movierc.filmpersonservice.repository.MovieRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class MovieEventConsumer {
    private final Logger logger = LoggerFactory.getLogger(MovieEventConsumer.class);

    private final MovieRepository movieRepository;
    private final FilmPersonRepository filmPersonRepository;

    private final MovieEventMapper movieEventMapper;
    private final FilmPersonMovieAssignmentMapper filmPersonMovieAssignmentMapper;

    @RabbitListener(queues = "#{movieCreatedQueue.getName()}")
    public void handleMovieCreatedEvent(@Payload MovieCreatedEvent event) {
        if (!movieRepository.existsById(event.getId())) {
            movieRepository.save(movieEventMapper.movieCreatedEventToMovie(event));
        }

        event.getFilmPersonRoles().forEach(filmPersonRole -> {
                     filmPersonRepository.findById(filmPersonRole.getFilmPersonId()).ifPresent(filmPerson -> {
                        filmPerson.getMovies().add(filmPersonMovieAssignmentMapper.movieFilmPersonRoleToFilmPersonMovieAssignment(filmPersonRole));
                        filmPersonRepository.save(filmPerson);
                    });
        });

        logger.info("Received Movie Created: {}", event);
    }

    @RabbitListener(queues = "#{movieUpdatedQueue.getName()}")
    public void handleMovieUpdatedEvent(@Payload MovieUpdatedEvent event) {
        if (movieRepository.existsById(event.getId())) {
            movieRepository.save(movieEventMapper.movieUpdatedEventToMovie(event));
        }

        logger.info("Received Movie Updated: {}", event);
    }

    @RabbitListener(queues = "#{movieDeletedQueue.getName()}")
    public void handleMovieDeletedEvent(@Payload MovieCreatedEvent event) {
        if (movieRepository.existsById(event.getId())) {
            movieRepository.deleteById(event.getId());
        }

        event.getFilmPersonRoles().forEach(filmPersonRole -> {
            filmPersonRepository.findById(filmPersonRole.getFilmPersonId()).ifPresent(filmPerson -> {
                filmPerson.getMovies().removeIf(m -> m.getMovieId().equals(event.getId()));
                filmPersonRepository.save(filmPerson);
            });
        });

        logger.info("Received Movie Deleted: {}", event);
    }

    @RabbitListener(queues = "#{movieFilmPersonRolesRemovedQueue.getName()}")
    public void handleMovieFilmPersonRolesAddedEvent(@Payload MovieFilmPersonRolesAddedEvent event) {
        event.getAddedMovieFilmPersonRoles().forEach(movieFilmPersonRole ->
                filmPersonRepository.findById(movieFilmPersonRole.getFilmPersonId()).ifPresent(filmPerson -> {
            filmPerson.getMovies().add(filmPersonMovieAssignmentMapper.movieFilmPersonRoleToFilmPersonMovieAssignment(movieFilmPersonRole));
            filmPersonRepository.save(filmPerson);
        }));

        logger.info("Received Movie FilmPersonRolesAdded: {}", event);
    }

    @RabbitListener(queues = "#{movieFilmPersonRolesRemovedQueue.getName()}")
    public void handleMovieFilmPersonRolesRemovedEvent(@Payload MovieFilmPersonRolesRemovedEvent event) {
        event.getRemovedMovieFilmPersonRoles().forEach(movieFilmPersonRole ->
                filmPersonRepository.findById(movieFilmPersonRole.getFilmPersonId()).ifPresent(filmPerson -> {
            filmPerson.getMovies().removeIf(m -> m.getMovieId().equals(movieFilmPersonRole.getFilmPersonId()));
            filmPersonRepository.save(filmPerson);
        }));

        logger.info("Received Movie FilmPersonRolesRemoved: {}", event);
    }
}
