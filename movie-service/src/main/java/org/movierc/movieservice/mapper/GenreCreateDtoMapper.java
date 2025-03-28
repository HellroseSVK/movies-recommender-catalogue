package org.movierc.movieservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.movierc.movieservice.dto.GenreCreateDto;
import org.movierc.movieservice.entity.Genre;

import java.util.UUID;

@Mapper(
        imports = {UUID.class}
)
public interface GenreCreateDtoMapper {
    @Mapping(target = "id", expression = "java(UUID.randomUUID())")
    Genre genreCreateDtoToGenre(GenreCreateDto genre);
}
