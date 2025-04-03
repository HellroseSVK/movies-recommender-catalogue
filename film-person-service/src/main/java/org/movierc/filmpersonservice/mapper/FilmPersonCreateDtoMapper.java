package org.movierc.filmpersonservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.movierc.filmpersonservice.dto.FilmPersonCreateDto;
import org.movierc.filmpersonservice.entity.FilmPerson;
import org.movierc.filmpersonservice.entity.FilmPersonMovieAssignment;

import java.util.Collections;
import java.util.UUID;

@Mapper(
    imports = {UUID.class, Collections.class, FilmPersonMovieAssignment.class}
)
public interface FilmPersonCreateDtoMapper {
    @Mapping(target = "id", expression = "java(UUID.randomUUID())")
    @Mapping(target = "movies", expression = "java(Collections.<FilmPersonMovieAssignment>emptyList())")
    FilmPerson filmPersonCreateDtoToMovie(FilmPersonCreateDto movie);
}