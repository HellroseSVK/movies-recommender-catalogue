package org.movierc.movieservice.exception;

import org.movierc.movieservice.entity.FilmPerson;
import org.movierc.shared.enm.FilmPersonRole;

public class FilmPersonWithThisRoleIsAlreadyAssigned extends RuntimeException {
    public FilmPersonWithThisRoleIsAlreadyAssigned(FilmPerson filmPerson, FilmPersonRole filmPersonRole) {
        super("Film person " + filmPerson.getName() + " (" + filmPerson.getId() + ") is a " + filmPersonRole);
    }
}
