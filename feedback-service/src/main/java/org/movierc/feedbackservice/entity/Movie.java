package org.movierc.feedbackservice.entity;

import lombok.*;

import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Movie {
    private UUID id;
    private String title;
}
