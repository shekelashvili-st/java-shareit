package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.exception.ItemUnavailableException;
import ru.practicum.shareit.exception.NoItemsOwnedException;
import ru.practicum.shareit.exception.UserPermissionsException;

import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Валидация теперь в gateway, здесь проверять нечего?
@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingService service;

    @Autowired
    private MockMvc mvc;

    @Test
    void testCreateBooking() throws Exception {
        BookingDto createDto = new BookingDto();
        createDto.setId(1L);
        createDto.setStart(Timestamp.from(Instant.now()));
        createDto.setEnd(Timestamp.from(Instant.now().plusMillis(10)));
        when(service.create(any(), anyLong())).thenReturn(createDto);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(createDto))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(createDto.getId()), Long.class));
    }

    @Test
    void testCreateBookingWrongId() throws Exception {
        BookingDto createDto = new BookingDto();
        createDto.setId(1L);
        createDto.setStart(Timestamp.from(Instant.now()));
        createDto.setEnd(Timestamp.from(Instant.now().plusMillis(10)));
        when(service.create(any(), anyLong())).thenThrow(IdNotFoundException.class);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(createDto))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testCreateBookingUnavailable() throws Exception {
        BookingDto createDto = new BookingDto();
        createDto.setId(1L);
        createDto.setStart(Timestamp.from(Instant.now()));
        createDto.setEnd(Timestamp.from(Instant.now().plusMillis(10)));
        when(service.create(any(), anyLong())).thenThrow(ItemUnavailableException.class);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(createDto))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testApprove() throws Exception {
        BookingDto createDto = new BookingDto();
        createDto.setId(1L);
        createDto.setStart(Timestamp.from(Instant.now()));
        createDto.setEnd(Timestamp.from(Instant.now().plusMillis(10)));
        when(service.approve(anyLong(), anyLong(), anyBoolean())).thenReturn(createDto);

        mvc.perform(patch("/bookings/" + createDto.getId() + "?approved=true")
                        .content(mapper.writeValueAsString(null))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testApproveNoPermission() throws Exception {
        BookingDto createDto = new BookingDto();
        createDto.setId(1L);
        createDto.setStart(Timestamp.from(Instant.now()));
        createDto.setEnd(Timestamp.from(Instant.now().plusMillis(10)));
        when(service.approve(anyLong(), anyLong(), anyBoolean())).thenThrow(UserPermissionsException.class);

        mvc.perform(patch("/bookings/" + createDto.getId() + "?approved=true")
                        .content(mapper.writeValueAsString(null))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testGetAllForOwner() throws Exception {
        BookingDto createDto = new BookingDto();
        createDto.setId(1L);
        createDto.setStart(Timestamp.from(Instant.now()));
        createDto.setEnd(Timestamp.from(Instant.now().plusMillis(10)));
        when(service.getByOwnerId(anyLong(), any())).thenReturn(List.of(createDto));

        mvc.perform(get("/bookings/owner?state=ALL")
                        .content(mapper.writeValueAsString(null))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllForOwnerNoItems() throws Exception {
        BookingDto createDto = new BookingDto();
        createDto.setId(1L);
        createDto.setStart(Timestamp.from(Instant.now()));
        createDto.setEnd(Timestamp.from(Instant.now().plusMillis(10)));
        when(service.getByOwnerId(anyLong(), any())).thenThrow(NoItemsOwnedException.class);

        mvc.perform(get("/bookings/owner?state=ALL")
                        .content(mapper.writeValueAsString(null))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }
}