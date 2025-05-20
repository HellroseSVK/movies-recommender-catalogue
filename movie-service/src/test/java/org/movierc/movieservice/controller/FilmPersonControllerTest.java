package org.movierc.movieservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.movierc.movieservice.dto.AssignFilmPersonRoleDto;
import org.movierc.movieservice.dto.MovieDto;
import org.movierc.movieservice.dto.RemoveFilmPersonRoleDto;
import org.movierc.movieservice.facade.MovieFacade;
import org.movierc.shared.enm.FilmPersonRole;
import org.movierc.shared.security.Authorities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FilmPersonController.class)
class FilmPersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MovieFacade movieFacade;

    @Test
    void assignFilmPerson_ValidInput_ReturnsUpdatedMovieDto() throws Exception {
        // Setup
        UUID movieId = UUID.randomUUID();
        AssignFilmPersonRoleDto assignDto = AssignFilmPersonRoleDto.builder()
                .filmPersonId(UUID.randomUUID())
                .role(FilmPersonRole.ACTOR)
                .build();
        MovieDto movieDto = MovieDto.builder()
                .id(movieId)
                .title("Test Movie")
                .build();
        when(movieFacade.assignFilmPerson(ArgumentMatchers.eq(movieId), ArgumentMatchers.any(AssignFilmPersonRoleDto.class)))
                .thenReturn(movieDto);

        // Act & Verify
        mockMvc.perform(post("/api/v1/movie/{movieId}/filmPerson", movieId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assignDto))
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(Authorities.WRITE_SCOPE_MOVIE)))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(movieId.toString())));

        verify(movieFacade).assignFilmPerson(ArgumentMatchers.eq(movieId), ArgumentMatchers.any(AssignFilmPersonRoleDto.class));
    }

    @Test
    void assignFilmPerson_MissingRequiredField_ReturnsBadRequest() throws Exception {
        // Setup: omit mandatory "filmPersonId"
        AssignFilmPersonRoleDto assignDto = AssignFilmPersonRoleDto.builder()
                .role(FilmPersonRole.ACTOR)
                .build();

        // Act & Verify
        mockMvc.perform(post("/api/v1/movie/{movieId}/filmPerson", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assignDto))
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(Authorities.WRITE_SCOPE_MOVIE)))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void removeFilmPerson_ValidInput_ReturnsUpdatedMovieDto() throws Exception {
        // Setup
        UUID movieId = UUID.randomUUID();
        RemoveFilmPersonRoleDto removeDto = RemoveFilmPersonRoleDto.builder()
                .filmPersonId(UUID.randomUUID())
                .role(FilmPersonRole.ACTOR)
                .build();
        MovieDto movieDto = MovieDto.builder()
                .id(movieId)
                .title("Test Movie")
                .build();
        when(movieFacade.removeFilmPerson(ArgumentMatchers.eq(movieId), ArgumentMatchers.any(RemoveFilmPersonRoleDto.class)))
                .thenReturn(movieDto);

        // Act & Verify
        mockMvc.perform(delete("/api/v1/movie/{movieId}/filmPerson", movieId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(removeDto))
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(Authorities.WRITE_SCOPE_MOVIE)))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(movieId.toString())));

        verify(movieFacade).removeFilmPerson(ArgumentMatchers.eq(movieId), ArgumentMatchers.any(RemoveFilmPersonRoleDto.class));
    }

    @Test
    void removeFilmPerson_MissingRequiredField_ReturnsBadRequest() throws Exception {
        // Setup: omit mandatory "filmPersonId"
        RemoveFilmPersonRoleDto removeDto = RemoveFilmPersonRoleDto.builder()
                .role(FilmPersonRole.ACTOR)
                .build();

        // Act & Verify
        mockMvc.perform(delete("/api/v1/movie/{movieId}/filmPerson", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(removeDto))
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(Authorities.WRITE_SCOPE_MOVIE)))
                )
                .andExpect(status().isBadRequest());
    }
}
