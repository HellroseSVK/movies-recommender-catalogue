package org.movierc.movieservice.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.movierc.movieservice.dto.MovieCreateDto;
import org.movierc.movieservice.entity.Movie;

import java.util.UUID;

@Mapper(
        imports = {UUID.class}
)
public interface MovieCreateDtoMapper {
    @Mapping(target = "id", expression = "java(UUID.randomUUID())")
    @Mapping(target = "numberOfRatings", constant = "0")
    @Mapping(target = "totalRating", constant = "0")
    @Mapping(target = "genres", ignore = true)
    @Mapping(target = "cast", ignore = true)
    @Mapping(target = "director", ignore = true)
    @Mapping(target = "screenwriter", ignore = true)
    @Mapping(target = "cinematographer", ignore = true)
    @Mapping(target = "composer", ignore = true)
    @Mapping(target = "sourceMaterialAuthor", ignore = true)
    Movie movieCreateDtoToMovie(MovieCreateDto movie);
}
