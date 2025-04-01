package org.movierc.filmpersonservice.repository;

import org.movierc.filmpersonservice.entity.FilmPerson;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class FilmPersonRepository {
    private final List<FilmPerson> filmPersons = new ArrayList<>();

    public List<FilmPerson> findAll() {
        return new ArrayList<>(filmPersons);
    }

    public Optional<FilmPerson> findById(UUID id) {
        return filmPersons.stream().filter(fp -> fp.getId().equals(id)).findFirst();
    }

    public boolean existsById(UUID id) {
        return filmPersons.stream().anyMatch(m -> m.getId().equals(id));
    }

    public void deleteById(UUID id) {
        filmPersons.removeIf(m -> m.getId().equals(id));
    }

    public FilmPerson save(FilmPerson filmPerson) {
        if (filmPersons.stream().anyMatch(fp -> fp.getId().equals(filmPerson.getId()))) {
            filmPersons.removeIf(fp -> fp.getId().equals(filmPerson.getId()));
        }

        filmPersons.add(filmPerson);
        return filmPerson;
    }

}
