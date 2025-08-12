package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Валидация теперь в gateway, здесь проверять нечего?
@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemService service;

    @Autowired
    private MockMvc mvc;

    @Test
    void testCreateItem() throws Exception {
        ItemDto createDto = new ItemDto();
        createDto.setId(1L);
        createDto.setName("name");
        createDto.setDescription("Desc");
        createDto.setAvailable(true);
        when(service.create(any(), anyLong())).thenReturn(createDto);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(createDto))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(createDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(createDto.getName())))
                .andExpect(jsonPath("$.description", is(createDto.getDescription())))
                .andExpect(jsonPath("$.available", is(true)));
    }

    @Test
    void testCreateItemWrongId() throws Exception {
        ItemDto createDto = new ItemDto();
        createDto.setId(1L);
        createDto.setName("name");
        createDto.setDescription("Desc");
        createDto.setAvailable(true);
        when(service.create(any(), anyLong())).thenThrow(IdNotFoundException.class);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(createDto))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testUpdateItem() throws Exception {
        ItemDto createDto = new ItemDto();
        createDto.setId(1L);
        createDto.setName("name");
        createDto.setDescription("Desc");
        createDto.setAvailable(true);
        when(service.update(any(), anyLong(), anyLong())).thenReturn(createDto);

        mvc.perform(patch("/items/" + createDto.getId())
                        .content(mapper.writeValueAsString(createDto))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(createDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(createDto.getName())))
                .andExpect(jsonPath("$.description", is(createDto.getDescription())))
                .andExpect(jsonPath("$.available", is(true)));
    }

    @Test
    void testUpdateItemWrongId() throws Exception {
        ItemDto createDto = new ItemDto();
        createDto.setId(1L);
        createDto.setName("name");
        createDto.setDescription("Desc");
        createDto.setAvailable(true);
        when(service.update(any(), anyLong(), anyLong())).thenThrow(IdNotFoundException.class);

        mvc.perform(patch("/items/" + createDto.getId())
                        .content(mapper.writeValueAsString(createDto))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testFindItem() throws Exception {
        ItemWithBookingsDto createDto = new ItemWithBookingsDto();
        createDto.setId(1L);
        createDto.setName("name");
        createDto.setDescription("Desc");
        createDto.setAvailable(true);
        when(service.findById(anyLong())).thenReturn(createDto);

        mvc.perform(get("/items/" + createDto.getId())
                        .content(mapper.writeValueAsString(createDto))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(createDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(createDto.getName())))
                .andExpect(jsonPath("$.description", is(createDto.getDescription())))
                .andExpect(jsonPath("$.available", is(true)));
    }

    @Test
    void testFindItemWrongId() throws Exception {
        ItemWithBookingsDto createDto = new ItemWithBookingsDto();
        createDto.setId(1L);
        createDto.setName("name");
        createDto.setDescription("Desc");
        createDto.setAvailable(true);
        when(service.findById(anyLong())).thenThrow(IdNotFoundException.class);

        mvc.perform(get("/items/" + createDto.getId())
                        .content(mapper.writeValueAsString(createDto))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

}