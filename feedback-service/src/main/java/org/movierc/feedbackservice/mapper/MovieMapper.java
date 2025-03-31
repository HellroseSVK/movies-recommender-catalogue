package org.movierc.feedbackservice.mapper;

import org.mapstruct.Mapper;
import org.movierc.feedbackservice.dto.MovieDto;
import org.movierc.feedbackservice.entity.Movie;

@Mapper
public interface MovieMapper {
    MovieDto movieToMovieDto(Movie movie);
}
