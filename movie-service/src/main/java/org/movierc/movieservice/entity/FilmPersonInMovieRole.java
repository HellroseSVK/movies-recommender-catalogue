package org.movierc.movieservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.movierc.shared.enm.FilmPersonRole;

import java.util.Objects;
import java.util.UUID;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FilmPersonInMovieRole {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    private Movie movie;
    @ManyToOne
    private FilmPerson filmPerson;
    private FilmPersonRole role;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        FilmPersonInMovieRole that = (FilmPersonInMovieRole) o;
        return (this.id == null || that.id == null || this.id == that.id) && role == that.role;
    }

    @Override
    public int hashCode() {
        if (this.id != null) {
            return Objects.hash(this.id, role);
        }
        return Objects.hashCode(role);
    }
}
