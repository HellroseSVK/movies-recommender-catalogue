package org.movierc.movieservice.repository;

import org.movierc.movieservice.entity.FilmPerson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FilmPersonRepository extends JpaRepository<FilmPerson, UUID> {
}
