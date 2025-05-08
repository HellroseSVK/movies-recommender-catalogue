package org.movierc.feedbackservice.facade;

import lombok.AllArgsConstructor;
import org.movierc.feedbackservice.artemis.UserMovieRatingEventArtemisProducer;
import org.movierc.feedbackservice.dto.UserMovieRatingCreateDto;
import org.movierc.feedbackservice.dto.UserMovieRatingDto;
import org.movierc.feedbackservice.dto.UserMovieRatingUpdateDto;
import org.movierc.feedbackservice.mapper.MovieRatingMapper;
import org.movierc.feedbackservice.mapper.RatingDetailMapper;
import org.movierc.feedbackservice.mapper.UserMovieRatingCreateDtoMapper;
import org.movierc.feedbackservice.rabbit.UserMovieRatingEventProducer;
import org.movierc.feedbackservice.service.UserMovieRatingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class UserMovieRatingFacade {
    private final UserMovieRatingService userMovieRatingService;

    private final MovieRatingMapper movieRatingMapper;
    private final UserMovieRatingCreateDtoMapper userMovieRatingCreateDtoMapper;
    private final RatingDetailMapper ratingDetailMapper;

    private final UserMovieRatingEventProducer userMovieRatingEventProducer;
    private final UserMovieRatingEventArtemisProducer userMovieRatingEventArtemisProducer;

    @Transactional(readOnly = true)
    public List<UserMovieRatingDto> getAllRatings() {
        return userMovieRatingService.getAllRatings().stream()
                .map(movieRatingMapper::userMovieRatingTouserMovieRatingDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserMovieRatingDto> getRatingsByUserId(String userId) {
        return userMovieRatingService.getRatingsByUserId(userId).stream()
                .map(movieRatingMapper::userMovieRatingTouserMovieRatingDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserMovieRatingDto> getRatingsByMovieId(UUID movieId) {
        return userMovieRatingService.getRatingsByMovieId(movieId).stream()
                .map(movieRatingMapper::userMovieRatingTouserMovieRatingDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserMovieRatingDto getRatingById(UUID id) {
        return movieRatingMapper.userMovieRatingTouserMovieRatingDto(userMovieRatingService.getRatingById(id));
    }

    public UserMovieRatingDto createRating(UserMovieRatingCreateDto userMovieRatingCreateDto) {
        var dto = userMovieRatingCreateDtoMapper.userMovieRatingCreateDtoToUserMovieRating(userMovieRatingCreateDto);
        dto.setRatingDetails(userMovieRatingCreateDto.getRatingDetails().stream().map(ratingDetailMapper::ratingDetailCreateDtoToRatingDetail).toList());

        var userMovieRating = userMovieRatingService.createRating(dto, userMovieRatingCreateDto.getMovieId());

        userMovieRatingEventProducer.sendMovieRatingCreatedEvent(
                movieRatingMapper.userMovieRatingToMovieRatingCreatedEvent(userMovieRating)
        );
        userMovieRatingEventArtemisProducer.sendMovieRatingChangedEvent(
                movieRatingMapper.userMovieRatingToMovieRatingChangedEvent(userMovieRating)
        );

        return movieRatingMapper.userMovieRatingTouserMovieRatingDto(userMovieRating);
    }

    public UserMovieRatingDto updateRating(UUID id, UserMovieRatingUpdateDto userMovieRatingUpdateDto) {
        var userMovieRating = userMovieRatingService.updateRating(
                id,
                userMovieRatingUpdateDto.getUserId(),
                userMovieRatingUpdateDto.getMovieId(),
                userMovieRatingUpdateDto.getRatingDetails().stream()
                        .map(ratingDetailMapper::ratingDetailDtoToRatingDetail)
                        .collect(Collectors.toList()),
                userMovieRatingUpdateDto.getComment()
        );

        userMovieRatingEventProducer.sendMovieRatingUpdatedEvent(
                movieRatingMapper.userMovieRatingToMovieRatingUpdatedEvent(userMovieRating)
        );
        userMovieRatingEventArtemisProducer.sendMovieRatingChangedEvent(
                movieRatingMapper.userMovieRatingToMovieRatingChangedEvent(userMovieRating)
        );

        return movieRatingMapper.userMovieRatingTouserMovieRatingDto(userMovieRating);
    }

    public void deleteRating(UUID id) {
        var userMovieRating = userMovieRatingService.deleteRating(id);

        userMovieRatingEventProducer.sendMovieRatingDeletedEvent(
                movieRatingMapper.userMovieRatingToMovieRatingDeletedEvent(userMovieRating)
        );
        userMovieRatingEventArtemisProducer.sendMovieRatingChangedEvent(
                movieRatingMapper.userMovieRatingToMovieRatingChangedEvent(userMovieRating)
        );
    }
}
