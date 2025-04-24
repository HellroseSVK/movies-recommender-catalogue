package org.movierc.movieservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.movierc.movieservice.dto.MovieCreateDto;
import org.movierc.movieservice.entity.Movie;

import java.util.UUID;

@Mapper(
        imports = {UUID.class}
)
public interface MovieCreateDtoMapper {
    @Mapping(target = "genres", ignore = true)
    @Mapping(target = "filmPersonRoles", ignore = true)
    Movie movieCreateDtoToMovie(MovieCreateDto movie);
}
