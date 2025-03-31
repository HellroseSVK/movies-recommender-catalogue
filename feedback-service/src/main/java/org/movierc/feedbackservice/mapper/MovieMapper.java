package org.movierc.feedbackservice.mapper;

import org.mapstruct.Mapper;
import org.movierc.feedbackservice.dto.MovieDto;

@Mapper
public interface MovieMapper {
    MovieDto movieToMovieDto(org.movierc.feedbackservice.entity.Movie movie);
}
