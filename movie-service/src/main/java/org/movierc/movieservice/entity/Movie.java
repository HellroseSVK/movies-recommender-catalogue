package org.movierc.movieservice.entity;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Movie {
    @ElementCollection
    private final Map<UUID, Integer> totalRatings = new HashMap<>();
    @ElementCollection
    private final Map<UUID, Integer> numbersOfRatings = new HashMap<>();
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String title;
    private String description;

    @ManyToMany(mappedBy = "movies", fetch = FetchType.LAZY)
    private List<Genre> genres;
    @ElementCollection
    private List<URL> images;
    @OneToMany(mappedBy = "movie", fetch = FetchType.LAZY)
    private List<FilmPersonInMovieRole> filmPersonRoles;

    public float getRating() {
        int totalRating = totalRatings.values().stream().mapToInt(Integer::intValue).sum();
        int numberOfRatings = numbersOfRatings.values().stream().mapToInt(Integer::intValue).sum();

        if (numberOfRatings == 0) {
            return 0;
        }
        return (float) totalRating / numberOfRatings;
    }
}
