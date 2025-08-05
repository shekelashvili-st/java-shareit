package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.EmailAlreadyExistsException;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Валидация теперь в gateway, здесь проверять нечего?
@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserService service;

    @Autowired
    private MockMvc mvc;

    @Test
    void testSaveUser() throws Exception {
        UserDto createDto = new UserDto();
        createDto.setId(1L);
        createDto.setName("name");
        createDto.setEmail("email@email.com");
        when(service.create(any())).thenReturn(createDto);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(createDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(createDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(createDto.getName())))
                .andExpect(jsonPath("$.email", is(createDto.getEmail())));
    }

    @Test
    void testSaveUserDuplicateEmail() throws Exception {
        UserDto createDto = new UserDto();
        createDto.setId(1L);
        createDto.setName("name");
        createDto.setEmail("email@email.com");
        when(service.create(any())).thenThrow(EmailAlreadyExistsException.class);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(createDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testUpdateUser() throws Exception {
        UserDto createDto = new UserDto();
        createDto.setId(1L);
        createDto.setName("name");
        createDto.setEmail("email@email.com");
        when(service.update(any(), anyLong())).thenReturn(createDto);

        mvc.perform(patch("/users/" + createDto.getId())
                        .content(mapper.writeValueAsString(createDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(createDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(createDto.getName())))
                .andExpect(jsonPath("$.email", is(createDto.getEmail())));
    }

    @Test
    void testUpdateUserNotFound() throws Exception {
        UserDto createDto = new UserDto();
        createDto.setId(1L);
        createDto.setName("name");
        createDto.setEmail("email@email.com");
        when(service.update(any(), anyLong())).thenThrow(IdNotFoundException.class);

        mvc.perform(patch("/users/" + createDto.getId())
                        .content(mapper.writeValueAsString(createDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testGetUser() throws Exception {
        UserDto createDto = new UserDto();
        createDto.setId(1L);
        createDto.setName("name");
        createDto.setEmail("email@email.com");
        when(service.getById(anyLong())).thenReturn(createDto);

        mvc.perform(get("/users/" + createDto.getId())
                        .content(mapper.writeValueAsString(createDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(createDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(createDto.getName())))
                .andExpect(jsonPath("$.email", is(createDto.getEmail())));
    }

    @Test
    void testGetUserNotFound() throws Exception {
        UserDto createDto = new UserDto();
        createDto.setId(1L);
        createDto.setName("name");
        createDto.setEmail("email@email.com");
        when(service.getById(anyLong())).thenThrow(IdNotFoundException.class);

        mvc.perform(get("/users/" + createDto.getId())
                        .content(mapper.writeValueAsString(createDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

    }

    @Test
    void testDeleteUser() throws Exception {
        UserDto createDto = new UserDto();
        createDto.setId(1L);
        createDto.setName("name");
        createDto.setEmail("email@email.com");

        mvc.perform(delete("/users/" + createDto.getId())
                        .content(mapper.writeValueAsString(createDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}