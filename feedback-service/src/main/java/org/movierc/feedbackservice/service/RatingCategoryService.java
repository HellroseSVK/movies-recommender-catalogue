package org.movierc.feedbackservice.service;

import lombok.AllArgsConstructor;
import org.movierc.feedbackservice.dto.RatingCategoryCreateDto;
import org.movierc.feedbackservice.dto.RatingCategoryDto;
import org.movierc.feedbackservice.exception.RatingCategoryNotFoundException;
import org.movierc.feedbackservice.mapper.RatingCategoryCreateDtoMapper;
import org.movierc.feedbackservice.mapper.RatingCategoryMapper;
import org.movierc.feedbackservice.rabbit.RatingCategoryEventProducer;
import org.movierc.feedbackservice.rabbit.UserMovieRatingEventProducer;
import org.movierc.feedbackservice.repository.RatingCategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RatingCategoryService {
    private final RatingCategoryRepository ratingCategoryRepository;
    private final RatingCategoryMapper ratingCategoryMapper;
    private final RatingCategoryCreateDtoMapper ratingCategoryCreateDtoMapper;
    private final RatingCategoryEventProducer ratingCategoryEventProducer;

    public List<RatingCategoryDto> getAllRatingCategories() {
        return ratingCategoryRepository.findAll().stream()
                .map(ratingCategoryMapper::ratingCategoryToRatingCategoryDto)
                .collect(Collectors.toList());
    }

    public RatingCategoryDto getRatingCategoryById(UUID id) {
        return ratingCategoryRepository.findById(id)
                .map(ratingCategoryMapper::ratingCategoryToRatingCategoryDto)
                .orElseThrow(() -> new RatingCategoryNotFoundException(id));
    }

    public RatingCategoryDto createRatingCategory(RatingCategoryCreateDto ratingCategoryCreateDto) {
        var ratingCategory = ratingCategoryCreateDtoMapper.ratingCategoryCreateDtoToRatingCategory(ratingCategoryCreateDto);

        ratingCategoryEventProducer.sendRatingCategoryCreatedEvent(
                ratingCategoryMapper.ratingCategoryToRatingCategoryCreatedEvent(ratingCategory)
        );

        return ratingCategoryMapper.ratingCategoryToRatingCategoryDto(ratingCategoryRepository.save(ratingCategory));
    }
}
