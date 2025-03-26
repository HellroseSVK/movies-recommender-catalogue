package org.movierc.movieservice.repository;

import org.movierc.movieservice.entity.Genre;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public class GenreRepository {
    private final List<Genre> genres = new ArrayList<>();

    {
        genres.add(new Genre(UUID.fromString("894061B7-4D80-4537-891A-18F008515D5A"), "Action"));
    }

    public List<Genre> findByIdIn(Set<UUID> ids) {
        return genres.stream().filter(g -> ids.contains(g.getId())).toList();
    }

    public Optional<Genre> findById(UUID id) {
        return genres.stream().filter(g -> g.getId().equals(id)).findFirst();
    }

    public Genre save(Genre genre) {
        if (genres.stream().anyMatch(g -> g.getId().equals(genre.getId()))) {
            genres.removeIf(g -> g.getId().equals(genre.getId()));
        } else {
            genre.setId(UUID.randomUUID());
        }

        genres.add(genre);
        return genre;
    }

}
