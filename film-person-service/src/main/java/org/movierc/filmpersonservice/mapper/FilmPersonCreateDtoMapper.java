package org.movierc.filmpersonservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.movierc.filmpersonservice.dto.FilmPersonCreateDto;
import org.movierc.filmpersonservice.entity.FilmPerson;

import java.util.UUID;

@Mapper(
        imports = {UUID.class}
)
public interface FilmPersonCreateDtoMapper {
    @Mapping(target = "id", expression = "java(UUID.randomUUID())")
    @Mapping(target = "movies", ignore = true)
    FilmPerson filmPersonCreateDtoToMovie(FilmPersonCreateDto movie);
}
