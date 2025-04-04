package org.movierc.movieservice.repository;

import org.movierc.movieservice.entity.FilmPerson;
import org.springframework.stereotype.Repository;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public class FilmPersonRepository {
    private final List<FilmPerson> filmPersons = new ArrayList<>();

    public Optional<FilmPerson> findById(UUID id) {
        return filmPersons.stream().filter(fp -> fp.getId().equals(id)).findFirst();
    }

    public List<FilmPerson> findByIdIn(Set<UUID> ids) {
        return filmPersons.stream().filter(fp -> ids.contains(fp.getId())).toList();
    }

    public FilmPerson save(FilmPerson filmPerson) {
        if (filmPersons.stream().anyMatch(fp -> fp.getId().equals(filmPerson.getId()))) {
            filmPersons.removeIf(fp -> fp.getId().equals(filmPerson.getId()));
        }

        filmPersons.add(filmPerson);
        return filmPerson;
    }

}
