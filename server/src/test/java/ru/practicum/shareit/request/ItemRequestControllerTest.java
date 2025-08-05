package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Валидация теперь в gateway, здесь проверять нечего?
@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemRequestService service;

    @Autowired
    private MockMvc mvc;

    @Test
    void testCreateRequest() throws Exception {
        ItemRequestDto createDto = new ItemRequestDto();
        createDto.setId(1L);
        createDto.setDescription("desc");
        when(service.create(any(), anyLong())).thenReturn(createDto);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(createDto))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(createDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(createDto.getDescription())));
    }

    @Test
    void testCreateRequestWrongId() throws Exception {
        ItemRequestDto createDto = new ItemRequestDto();
        createDto.setId(1L);
        createDto.setDescription("desc");
        when(service.create(any(), anyLong())).thenThrow(IdNotFoundException.class);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(createDto))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }
}