package org.movierc.feedbackservice.facade;

import lombok.AllArgsConstructor;
import org.movierc.feedbackservice.dto.UserMovieRatingCreateDto;
import org.movierc.feedbackservice.dto.UserMovieRatingDto;
import org.movierc.feedbackservice.dto.UserMovieRatingUpdateDto;
import org.movierc.feedbackservice.mapper.MovieRatingMapper;
import org.movierc.feedbackservice.mapper.RatingDetailDtoMapper;
import org.movierc.feedbackservice.mapper.UserMovieRatingCreateDtoMapper;
import org.movierc.feedbackservice.rabbit.UserMovieRatingEventProducer;
import org.movierc.feedbackservice.service.UserMovieRatingService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserMovieRatingFacade {
    private final UserMovieRatingService userMovieRatingService;

    private final MovieRatingMapper movieRatingMapper;
    private final UserMovieRatingCreateDtoMapper userMovieRatingCreateDtoMapper;
    private final RatingDetailDtoMapper ratingDetailDtoMapper;

    private final UserMovieRatingEventProducer userMovieRatingEventProducer;

    public List<UserMovieRatingDto> getAllRatings() {
        return userMovieRatingService.getAllRatings().stream()
                .map(movieRatingMapper::userMovieRatingTouserMovieRatingDto)
                .collect(Collectors.toList());
    }

    public List<UserMovieRatingDto> getRatingsByUserId(UUID userId) {
        return userMovieRatingService.getRatingsByUserId(userId).stream()
                .map(movieRatingMapper::userMovieRatingTouserMovieRatingDto)
                .collect(Collectors.toList());
    }

    public List<UserMovieRatingDto> getRatingsByMovieId(UUID movieId) {
        return userMovieRatingService.getRatingsByMovieId(movieId).stream()
                .map(movieRatingMapper::userMovieRatingTouserMovieRatingDto)
                .collect(Collectors.toList());
    }

    public UserMovieRatingDto getRatingById(UUID id) {
        return movieRatingMapper.userMovieRatingTouserMovieRatingDto(userMovieRatingService.getRatingById(id));
    }

    public UserMovieRatingDto createRating(UserMovieRatingCreateDto userMovieRatingCreateDto) {
        var userMovieRating = userMovieRatingService.createRating(
                userMovieRatingCreateDtoMapper.userMovieRatingCreateDtoToUserMovieRating(userMovieRatingCreateDto),
                userMovieRatingCreateDto.getUserId(),
                userMovieRatingCreateDto.getMovieId(),
                userMovieRatingCreateDto.getRatingDetails()
        ));

        userMovieRatingEventProducer.sendMovieRatingCreatedEvent(
                movieRatingMapper.userMovieRatingToMovieRatingCreatedEvent(userMovieRating)
        );

        return movieRatingMapper.userMovieRatingTouserMovieRatingDto(userMovieRating);
    }

    public UserMovieRatingDto updateRating(UUID id, UserMovieRatingUpdateDto userMovieRatingUpdateDto) {
        var userMovieRating =  userMovieRatingService.updateRating(
                id,
                userMovieRatingUpdateDto.getUserId(),
                userMovieRatingUpdateDto.getMovieId(),
                userMovieRatingUpdateDto.getRatingDetails().stream()
                        .map(ratingDetailDtoMapper::ratingDetailDtoToRatingDetail)
                        .collect(Collectors.toList()),
                userMovieRatingUpdateDto.getComment()
        );

        userMovieRatingEventProducer.sendMovieRatingUpdatedEvent(
                movieRatingMapper.userMovieRatingToMovieRatingUpdatedEvent(userMovieRating)
        );

        return movieRatingMapper.userMovieRatingTouserMovieRatingDto(userMovieRating);
    }

    public void deleteRating(UUID id) {
        var userMovieRating = userMovieRatingService.deleteRating(id);

        userMovieRatingEventProducer.sendMovieRatingDeletedEvent(
                movieRatingMapper.userMovieRatingToMovieRatingDeletedEvent(userMovieRating)
        );
    }
}
