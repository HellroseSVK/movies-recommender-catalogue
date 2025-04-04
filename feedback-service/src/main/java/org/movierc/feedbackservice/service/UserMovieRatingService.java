package org.movierc.feedbackservice.service;

import lombok.AllArgsConstructor;
import org.movierc.feedbackservice.dto.UserMovieRatingCreateDto;
import org.movierc.feedbackservice.dto.UserMovieRatingDto;
import org.movierc.feedbackservice.dto.UserMovieRatingUpdateDto;
import org.movierc.feedbackservice.exception.MovieNotFoundException;
import org.movierc.feedbackservice.exception.RatingCategoryNotFoundException;
import org.movierc.feedbackservice.exception.UserMovieRatingNotFoundException;
import org.movierc.feedbackservice.exception.UserNotFoundException;
import org.movierc.feedbackservice.mapper.MovieRatingMapper;
import org.movierc.feedbackservice.mapper.RatingDetailDtoMapper;
import org.movierc.feedbackservice.mapper.UserMovieRatingCreateDtoMapper;
import org.movierc.feedbackservice.rabbit.UserMovieRatingEventProducer;
import org.movierc.feedbackservice.repository.MovieRepository;
import org.movierc.feedbackservice.repository.RatingCategoryRepository;
import org.movierc.feedbackservice.repository.UserMovieRatingRepository;
import org.movierc.feedbackservice.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserMovieRatingService {
    private final UserMovieRatingRepository userMovieRatingRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final MovieRatingMapper movieRatingMapper;
    private final UserMovieRatingCreateDtoMapper userMovieRatingCreateDtoMapper;
    private final RatingDetailDtoMapper ratingDetailDtoMapper;
    private final UserMovieRatingEventProducer userMovieRatingEventProducer;
    private final RatingCategoryRepository ratingCategoryRepository;

    public List<UserMovieRatingDto> getAllRatings() {
        return userMovieRatingRepository.findAll().stream()
                .map(movieRatingMapper::userMovieRatingTouserMovieRatingDto)
                .collect(Collectors.toList());
    }

    public List<UserMovieRatingDto> getRatingsByUserId(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }

        return userMovieRatingRepository.findByUserId(userId).stream()
                .map(movieRatingMapper::userMovieRatingTouserMovieRatingDto)
                .collect(Collectors.toList());
    }

    public List<UserMovieRatingDto> getRatingsByMovieId(UUID movieId) {
        if (!movieRepository.existsById(movieId)) {
            throw new MovieNotFoundException(movieId);
        }

        return userMovieRatingRepository.findByMovieId(movieId).stream()
                .map(movieRatingMapper::userMovieRatingTouserMovieRatingDto)
                .collect(Collectors.toList());
    }

    public UserMovieRatingDto getRatingById(UUID id) {
        return userMovieRatingRepository.findById(id)
                .map(movieRatingMapper::userMovieRatingTouserMovieRatingDto)
                .orElseThrow(() -> new UserMovieRatingNotFoundException(id));
    }

    public UserMovieRatingDto createRating(UserMovieRatingCreateDto userMovieRatingCreateDto) {
        var userMovieRating = userMovieRatingCreateDtoMapper.userMovieRatingCreateDtoToUserMovieRating(userMovieRatingCreateDto);

        userMovieRating.setUser(
                userRepository.findById(userMovieRatingCreateDto.getUserId())
                        .orElseThrow(() -> new UserNotFoundException(userMovieRatingCreateDto.getUserId()))
        );
        userMovieRating.setMovie(
                movieRepository.findById(userMovieRatingCreateDto.getMovieId())
                        .orElseThrow(() -> new MovieNotFoundException(userMovieRatingCreateDto.getMovieId()))
        );
        userMovieRating.getRatingDetails().forEach(
                ratingDetail -> ratingCategoryRepository.findById(ratingDetail.getRatingCategoryId())
                        .orElseThrow(() -> new RatingCategoryNotFoundException(ratingDetail.getRatingCategoryId()))
        );

        var savedUserMovieRating = userMovieRatingRepository.save(userMovieRating);

        userMovieRatingEventProducer.sendMovieRatingCreatedEvent(
                movieRatingMapper.userMovieRatingToMovieRatingCreatedEvent(savedUserMovieRating)
        );

        return movieRatingMapper.userMovieRatingTouserMovieRatingDto(savedUserMovieRating);
    }

    public UserMovieRatingDto updateRating(UUID id, UserMovieRatingUpdateDto userMovieRatingUpdateDto) {
        var userMovieRating = userMovieRatingRepository.findById(id)
                .orElseThrow(() -> new UserMovieRatingNotFoundException(id));

        userMovieRating.setUser(
                userRepository.findById(userMovieRatingUpdateDto.getUserId())
                        .orElseThrow(() -> new UserNotFoundException(userMovieRatingUpdateDto.getUserId()))
        );
        userMovieRating.setMovie(
                movieRepository.findById(userMovieRatingUpdateDto.getMovieId())
                        .orElseThrow(() -> new UserMovieRatingNotFoundException(userMovieRatingUpdateDto.getMovieId()))
        );
        userMovieRating.setRatingDetails(userMovieRatingUpdateDto.getRatingDetails().stream().map(
                ratingDetailDtoMapper::ratingDetailDtoToRatingDetail
        ).collect(Collectors.toList()));
        userMovieRating.setComment(userMovieRatingUpdateDto.getComment());

        var updatedUserMovieRating = userMovieRatingRepository.save(userMovieRating);

        userMovieRatingEventProducer.sendMovieRatingUpdatedEvent(
                movieRatingMapper.userMovieRatingToMovieRatingUpdatedEvent(updatedUserMovieRating)
        );

        return movieRatingMapper.userMovieRatingTouserMovieRatingDto(updatedUserMovieRating);
    }

    public void deleteRating(UUID id) {
        var userMovieRating = userMovieRatingRepository.findById(id)
                .orElseThrow(() -> new UserMovieRatingNotFoundException(id));

        userMovieRatingRepository.deleteById(id);

        userMovieRatingEventProducer.sendMovieRatingDeletedEvent(
                movieRatingMapper.userMovieRatingToMovieRatingDeletedEvent(userMovieRating)
        );
    }
}
