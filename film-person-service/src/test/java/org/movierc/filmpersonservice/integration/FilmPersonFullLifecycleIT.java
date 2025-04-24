package org.movierc.filmpersonservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.movierc.filmpersonservice.dto.FilmPersonCreateDto;
import org.movierc.filmpersonservice.dto.FilmPersonUpdateDto;
import org.movierc.filmpersonservice.entity.FilmPerson;
import org.movierc.filmpersonservice.repository.FilmPersonRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class FilmPersonFullLifecycleIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FilmPersonRepository filmPersonRepository;

    @MockitoBean
    private RabbitTemplate rabbitTemplate;

    private FilmPersonCreateDto createDto;

    @BeforeEach
    void setUp() {
        filmPersonRepository.deleteAll();
        createDto = new FilmPersonCreateDto();
        createDto.setName("Jane Doe");
        createDto.setDateOfBirth(LocalDate.of(1990, 1, 1));
    }

    @Test
    void fullCrudLifecycleTest_withMockedRabbitTemplate() throws Exception {
        // CREATE
        String response = mockMvc.perform(post("/api/v1/filmPerson")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        UUID id = UUID.fromString(objectMapper.readTree(response).get("id").asText());
        assertThat(filmPersonRepository.findById(id)).isPresent();

        verify(rabbitTemplate, times(1)).convertAndSend(
                ArgumentMatchers.any(String.class),
                ArgumentMatchers.any(String.class),
                ArgumentMatchers.any(Object.class)
        );

        // UPDATE
        FilmPersonUpdateDto updateDto = new FilmPersonUpdateDto();
        updateDto.setName("Janet Doe");

        mockMvc.perform(put("/api/v1/filmPerson/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Janet"));

        assertThat(filmPersonRepository.findById(id)).get().extracting(FilmPerson::getName).isEqualTo("Janet");

        verify(rabbitTemplate, times(2)).convertAndSend(
                ArgumentMatchers.any(String.class),
                ArgumentMatchers.any(String.class),
                ArgumentMatchers.any(Object.class)
        );

        // DELETE
        mockMvc.perform(delete("/api/v1/filmPerson/" + id))
                .andExpect(status().isNoContent());

        assertThat(filmPersonRepository.findById(id)).isEmpty();

        verify(rabbitTemplate, times(3)).convertAndSend(
                ArgumentMatchers.any(String.class),
                ArgumentMatchers.any(String.class),
                ArgumentMatchers.any(Object.class)
        );
    }
}
