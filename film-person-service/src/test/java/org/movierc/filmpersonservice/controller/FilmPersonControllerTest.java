package org.movierc.filmpersonservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.movierc.filmpersonservice.dto.FilmPersonCreateDto;
import org.movierc.filmpersonservice.dto.FilmPersonDto;
import org.movierc.filmpersonservice.dto.FilmPersonUpdateDto;
import org.movierc.filmpersonservice.exception.FilmPersonNotFoundException;
import org.movierc.filmpersonservice.facade.FilmPersonFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URL;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FilmPersonController.class)
class FilmPersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private FilmPersonFacade filmPersonFacade;

    @Test
    void getAllFilmPersons_FilmPersonsExist_ReturnsFilmPersonList() throws Exception {
        // Setup
        FilmPersonDto dto = FilmPersonDto.builder()
                .id(UUID.randomUUID())
                .name("John Doe")
                .image(new URL("http://image.png"))
                .dateOfBirth(LocalDate.of(1980, 1, 1))
                .biography("Bio")
                .build();
        when(filmPersonFacade.getAllFilmPersons()).thenReturn(List.of(dto));

        // Act & Verify
        mockMvc.perform(get("/api/v1/filmPerson"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("John Doe")));

        verify(filmPersonFacade).getAllFilmPersons();
    }

    @Test
    void getAllFilmPersons_NoFilmPersons_ReturnsEmptyList() throws Exception {
        // Setup
        when(filmPersonFacade.getAllFilmPersons()).thenReturn(Collections.emptyList());

        // Act & Verify
        mockMvc.perform(get("/api/v1/filmPerson"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(filmPersonFacade).getAllFilmPersons();
    }

    @Test
    void getFilmPersonById_FilmPersonFound_ReturnsFilmPersonDto() throws Exception {
        // Setup
        UUID id = UUID.randomUUID();
        FilmPersonDto dto = FilmPersonDto.builder()
                .id(id)
                .name("Jane Doe")
                .image(new URL("http://image.png"))
                .dateOfBirth(LocalDate.of(1990, 2, 2))
                .biography("Bio Jane")
                .build();
        when(filmPersonFacade.getFilmPersonById(id)).thenReturn(dto);

        // Act & Verify
        mockMvc.perform(get("/api/v1/filmPerson/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id.toString())))
                .andExpect(jsonPath("$.name", is("Jane Doe")));

        verify(filmPersonFacade).getFilmPersonById(id);
    }

    @Test
    void getFilmPersonById_FilmPersonNotFound_Returns404() throws Exception {
        // Setup
        UUID id = UUID.randomUUID();
        when(filmPersonFacade.getFilmPersonById(id)).thenThrow(new FilmPersonNotFoundException(id));

        // Act & Verify
        mockMvc.perform(get("/api/v1/filmPerson/{id}", id))
                .andExpect(status().isNotFound());

        verify(filmPersonFacade).getFilmPersonById(id);
    }

    // POST create film person
    @Test
    void createFilmPerson_ValidInput_ReturnsCreatedFilmPersonDto() throws Exception {
        // Setup
        FilmPersonCreateDto createDto = FilmPersonCreateDto.builder()
                .name("New Person")
                .image(new URL("http://image.png"))
                .dateOfBirth(LocalDate.of(2000, 3, 3))
                .biography("New biography")
                .build();
        FilmPersonDto returnedDto = FilmPersonDto.builder()
                .id(UUID.randomUUID())
                .name("New Person")
                .image(new URL("http://image.png"))
                .dateOfBirth(LocalDate.of(2000, 3, 3))
                .biography("New biography")
                .build();
        when(filmPersonFacade.createFilmPerson(ArgumentMatchers.any(FilmPersonCreateDto.class))).thenReturn(returnedDto);

        // Act & Verify
        mockMvc.perform(post("/api/v1/filmPerson")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("New Person")));

        verify(filmPersonFacade).createFilmPerson(ArgumentMatchers.any(FilmPersonCreateDto.class));
    }

    @Test
    void createFilmPerson_MissingRequiredField_ReturnsBadRequest() throws Exception {
        // Setup: omit mandatory "name"
        FilmPersonCreateDto createDto = FilmPersonCreateDto.builder()
                .image(new URL("http://image.png"))
                .dateOfBirth(LocalDate.of(2000, 3, 3))
                .biography("New biography")
                .build();

        // Act & Verify
        mockMvc.perform(post("/api/v1/filmPerson")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateFilmPerson_ValidInput_ReturnsUpdatedFilmPersonDto() throws Exception {
        // Setup
        UUID id = UUID.randomUUID();
        FilmPersonUpdateDto updateDto = FilmPersonUpdateDto.builder()
                .name("Updated Name")
                .image(new URL("http://image.png"))
                .dateOfBirth(LocalDate.of(1985, 5, 5))
                .biography("Updated Bio")
                .build();
        FilmPersonDto updatedDto = FilmPersonDto.builder()
                .id(id)
                .name("Updated Name")
                .image(new URL("http://image.png"))
                .dateOfBirth(LocalDate.of(1985, 5, 5))
                .biography("Updated Bio")
                .build();
        when(filmPersonFacade.updateFilmPerson(id, updateDto)).thenReturn(updatedDto);

        // Act & Verify
        mockMvc.perform(put("/api/v1/filmPerson/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Updated Name")));

        verify(filmPersonFacade).updateFilmPerson(id, updateDto);
    }

    @Test
    void updateFilmPerson_FilmPersonNotFound_Returns404() throws Exception {
        // Setup
        UUID id = UUID.randomUUID();
        FilmPersonUpdateDto updateDto = FilmPersonUpdateDto.builder()
                .name("Non existent")
                .build();
        when(filmPersonFacade.updateFilmPerson(id, updateDto))
                .thenThrow(new FilmPersonNotFoundException(id));

        // Act & Verify
        mockMvc.perform(put("/api/v1/filmPerson/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound());

        verify(filmPersonFacade).updateFilmPerson(id, updateDto);
    }

    @Test
    void deleteFilmPerson_ValidId_ReturnsNoContent() throws Exception {
        // Setup
        UUID id = UUID.randomUUID();

        // Act & Verify
        mockMvc.perform(delete("/api/v1/filmPerson/{id}", id))
                .andExpect(status().isNoContent());

        verify(filmPersonFacade).deleteFilmPerson(id);
    }

    @Test
    void deleteFilmPerson_FilmPersonNotFound_Returns404() throws Exception {
        // Setup
        UUID id = UUID.randomUUID();
        doThrow(new FilmPersonNotFoundException(id)).when(filmPersonFacade).deleteFilmPerson(id);

        // Act & Verify
        mockMvc.perform(delete("/api/v1/filmPerson/{id}", id))
                .andExpect(status().isNotFound());

        verify(filmPersonFacade).deleteFilmPerson(id);
    }
}
