package org.movierc.userservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.movierc.shared.enm.UserRole;
import org.movierc.userservice.dto.UserCreateDto;
import org.movierc.userservice.dto.UserDto;
import org.movierc.userservice.dto.UserUpdateDto;
import org.movierc.userservice.entity.User;
import org.movierc.userservice.rabbit.UserEventProducer;
import org.movierc.userservice.repository.UserRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserEventProducer userEventProducer;

    @MockitoBean
    private RabbitTemplate rabbitTemplate;

    @Test
    @Transactional
    void createUser_createsSuccessfully() throws Exception {
        // Setup
        String username = "testUser";
        String email = "testuser@example.com";
        String password = "password123";
        List<UserRole> roles = List.of(UserRole.USER);

        var request = UserCreateDto.builder()
                .username(username)
                .email(email)
                .password(password)
                .roles(roles)
                .build();
        // Act
        mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").exists())
                .andExpect(jsonPath("$.email").exists())
                .andExpect(jsonPath("$.password").exists());

        // Verify
        Optional<User> user = userRepository.findByUsername(username);
        assertTrue(user.isPresent());
        assertEquals(username, user.get().getUsername());
        assertEquals(email, user.get().getEmail());
        assertEquals(password, user.get().getPassword());

        verify(userEventProducer).sendUserCreatedEvent(any());
    }

    @Test
    @Transactional
    void getUserById_returnsUserSuccessfully() throws Exception {
        // Setup
        String username = "getUserTest";
        String email = "getuser@example.com";
        String password = "pass123";
        List<UserRole> roles = List.of(UserRole.USER);
        var createRequest = UserCreateDto.builder()
                .username(username)
                .email(email)
                .password(password)
                .roles(roles)
                .build();
        var mvcResult = mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();
        UserDto createdUser = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UserDto.class);

        // Act & Verify
        mockMvc.perform(get("/api/v1/user/" + createdUser.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.email").value(email));
    }

    @Test
    @Transactional
    void updateUser_updatesSuccessfully() throws Exception {
        // Setup
        String username = "updateUserTest";
        String email = "updateuser@example.com";
        String password = "oldpassword";
        List<UserRole> roles = List.of(UserRole.USER);

        var createRequest = UserCreateDto.builder()
                .username(username)
                .email(email)
                .password(password)
                .roles(roles)
                .build();

        var mvcResult = mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        UserDto createdUser = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UserDto.class);

        // Act
        String updatedUsername = "updatedUsername";
        String updatedEmail = "updateduser@example.com";
        var updateRequest = UserUpdateDto.builder()
                .username(updatedUsername)
                .email(updatedEmail)
                .password(password)
                .build();

        mockMvc.perform(put("/api/v1/user/" + createdUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(updatedUsername))
                .andExpect(jsonPath("$.email").value(updatedEmail));
    }

    @Test
    @Transactional
    void deleteUser_deletesSuccessfully() throws Exception {
        // Setup
        String username = "deleteUserTest";
        String email = "deleteuser@example.com";
        String password = "deletepass";
        List<UserRole> roles = List.of(UserRole.USER);
        var createRequest = UserCreateDto.builder()
                .username(username)
                .email(email)
                .password(password)
                .roles(roles)
                .build();
        var mvcResult = mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();
        UserDto createdUser = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UserDto.class);

        // Act
        mockMvc.perform(delete("/api/v1/user/" + createdUser.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // Verify
        Optional<User> user = userRepository.findByUsername(username);
        assertTrue(user.isEmpty());
    }

    @Test
    @Transactional
    void getAllUsers_returnsListSuccessfully() throws Exception {
        // Setup
        String username1 = "userOne";
        String email1 = "userone@example.com";
        String password1 = "pass1";
        List<UserRole> roles = List.of(UserRole.USER);
        var createRequest1 = UserCreateDto.builder()
                .username(username1)
                .email(email1)
                .password(password1)
                .roles(roles)
                .build();
        mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest1)))
                .andExpect(status().isCreated());

        String username2 = "userTwo";
        String email2 = "usertwo@example.com";
        String password2 = "pass2";
        var createRequest2 = UserCreateDto.builder()
                .username(username2)
                .email(email2)
                .password(password2)
                .roles(roles)
                .build();
        mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest2)))
                .andExpect(status().isCreated());

        // Act & Verify
        mockMvc.perform(get("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*].username").isNotEmpty());
    }
}
